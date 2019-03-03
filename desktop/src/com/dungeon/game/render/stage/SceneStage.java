package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.resource.Resources;

import java.util.Comparator;

public class SceneStage implements RenderStage {

	// Entities need to be rendered with premultiplied alpha onto the alpha-enabled buffer
	private static ShaderProgram entityShader = Resources.shaders.get("df_vertex.glsl|premultiplied_alpha_fragment.glsl");

	private final Comparator<? super Entity> comp = (e1, e2) ->
			e1.getZIndex() > e2.getZIndex() ? 1 :
			e1.getZIndex() < e2.getZIndex() ? -1 :
			e1.getOrigin().y > e2.getOrigin().y ? -1 :
			e1.getOrigin().y < e2.getOrigin().y ? 1 :
			e1.getOrigin().x < e2.getOrigin().x ? -1 :
			e1.getOrigin().x > e2.getOrigin().x ? 1 : 0;

	private final ViewPort viewPort;
	private final ViewPortBuffer output;
	private final ViewPortBuffer base;
	private final ViewPortBuffer lights;
	private final ViewPortBuffer current;
	private final BlendFunctionContext combineLights;
	private final BlendFunctionContext blendLights;
	private final BlendFunctionContext blendSprites;
	private boolean enabled = true;
	private final TextureRegion shadow;

	private static final float SHADOW_INTENSITY = 0.6f;
	private final Color shadowColor = new Color(1, 1, 1, SHADOW_INTENSITY);
	private final Color lightColor = Color.WHITE.cpy();
	private static final float MAX_HEIGHT_ATTENUATION = 100;
	private static final int VERTICAL_OFFSET = -2;

	private boolean drawTiles = true;
	private boolean drawEntities = true;
	private boolean drawShadows = true;
	private boolean drawLights = true;

	public SceneStage(ViewPort viewPort, ViewPortBuffer output) {
		this.viewPort = viewPort;
		// Output buffer
		this.output = output;
		// Base (unlit) buffer
		this.base = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		this.base.reset();
		// Lightmap buffer
		this.lights = new ViewPortBuffer(viewPort);
		this.lights.reset();
		// Separate current light buffer
		this.current = new ViewPortBuffer(viewPort);
		this.current.reset();
		this.current.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.combineLights = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE);
		this.blendLights = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.blendSprites = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.shadow = new TextureRegion(Resources.textures.get("shadow.png"));
	}

	@Override
	public void render() {
		if (!enabled) {
			output.render(batch -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
			return;
		}
		if (drawTiles) {
			// Draw tiles
			base.render(this::drawTiles);
		} else {
			base.render(batch -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}
		if (drawLights) {
			// Render lights (with shadows)
			lights.render(batch -> {
				Gdx.gl.glClearColor(Engine.getBaseLight().r, Engine.getBaseLight().g, Engine.getBaseLight().b, 1f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
			Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::lightIsInViewPort).filter(e -> e.getLight() != null).forEach(e -> drawLight(e, viewPort, drawShadows));
			// Combine tiles with lighting
			base.render(batch -> blendLights.run(batch, () -> lights.draw(batch)));
		}
		// Output lit tiles
		output.render(base::draw);

		if (drawEntities) {
			// Render entities in base buffer
			base.render(batch -> {
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
			base.render(batch -> blendSprites.run(batch, () -> {
				batch.setShader(entityShader);
				// Iterate entities in render order and draw them
				Engine.entities.inViewPort(viewPort).filter(viewPort::isInViewPort).sorted(comp).forEach(e -> e.draw(batch, viewPort));
			}));
			if (drawLights) {
				// Render lights (without shadows; don't want them to project over entities)
				lights.render(batch -> {
					Gdx.gl.glClearColor(Engine.getBaseLight().r, Engine.getBaseLight().g, Engine.getBaseLight().b, 1f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				});
				Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::lightIsInViewPort).filter(e -> e.getLight() != null).forEach(e -> drawLight(e, viewPort, false));
				// Combine tiles with lighting
				base.render(batch -> blendLights.run(batch, () -> lights.draw(batch)));
			}
			// Output lit entities
			output.render(base::draw);
		}

		// Draw flares
		output.render(batch -> combineLights.run(batch, () -> {
			Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::flareIsInViewPort).filter(e -> e.getFlare() != null).forEach(flare -> {
				lightColor.set(flare.getFlare().color).premultiplyAlpha().mul(flare.getFlare().dim);
				batch.setColor(lightColor);
				// Draw light texture
				viewPort.draw(batch, flare.getFlare().texture, flare.getOrigin().x, flare.getOrigin().y + flare.getZPos(), flare.getFlare().diameter * flare.getFlare().dim, flare.getFlare().angle);
			});
			batch.setColor(Color.WHITE);
		}));

	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	private void drawTiles(SpriteBatch batch) {
		// Only render the visible portion of the map
		int tSize = Game.getEnvironment().getTileset().tile_size;
		int minX = Math.max(0, viewPort.cameraX / tSize);
		int maxX = Math.min(Game.getLevel().getWidth() - 1, (viewPort.cameraX + viewPort.cameraWidth) / tSize) + 1;
		int minY = Math.max(0, viewPort.cameraY / tSize - 1);
		int maxY = Math.min(Game.getLevel().getHeight() - 1, (viewPort.cameraY + viewPort.cameraHeight) / tSize);
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				TextureRegion textureRegion = Game.getLevel().getAnimation(x, y).getKeyFrame(Engine.time(), true);
				batch.draw(textureRegion, (x * tSize - viewPort.cameraX), (y * tSize - viewPort.cameraY), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
				Game.getLevel().setDiscovered(x, y);
			}
		}
	}

	private void drawLight(Entity light, ViewPort viewPort, boolean drawShadows) {
		// Draw each light in a separate buffer
		current.render(batch -> {
			// Clear the buffer (black)
			Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			// Set light color
			lightColor.set(light.getLight().color).premultiplyAlpha().mul(light.getLight().dim);
			batch.setColor(lightColor);
			// Draw light texture
			viewPort.draw(batch, light.getLight().texture, light.getOrigin().x, light.getOrigin().y + light.getZPos(), light.getLight().diameter * light.getLight().dim, light.getLight().angle);
			// Draw shadows
			if (drawShadows) {
				Vector2 origin = light.getOrigin().cpy().add(0, light.getZPos());
				Engine.entities.radius(origin, light.getLight().diameter / 2).filter(Entity::castsShadow).forEach(blocker -> {
					// Draw shadow at the feet of the entity
					shadowColor.a = SHADOW_INTENSITY * blocker.getColor().a;
					batch.setColor(shadowColor);
					float attenuation = 1 - Math.min(blocker.getZPos(), MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
					float width = blocker.getBody().getBoundingBox().x * attenuation;
					float height = width / 3 * attenuation;

					viewPort.draw(batch,
							shadow,
							blocker.getBody().getBottomLeft().x,
							blocker.getBody().getBottomLeft().y + VERTICAL_OFFSET,
							width,
							height);

					// Draw projected shadow
					Vector2 offset = blocker.getOrigin().cpy().sub(light.getOrigin()).sub(0, light.getZPos());
					float shadowLen = offset.len();
					shadowColor.a = SHADOW_INTENSITY * Util.clamp(1 - shadowLen / 100f) * blocker.getColor().a;
					batch.setColor(shadowColor);
					batch.draw(
							shadow.getTexture(),
							blocker.getBody().getCenter().x - viewPort.cameraX,
							blocker.getBody().getBottomLeft().y - viewPort.cameraY,
							0,
							width / 2,
							width,
							width,
							shadowLen / 10f,
							1 + shadowLen / 100f,
							offset.angle(),
							0,
							0,
							shadow.getRegionWidth(),
							shadow.getRegionHeight(),
							false,
							false);
				});
			}
		});
		// And then combine that into the main buffer
		lights.render(batch -> combineLights.run(batch, () -> current.draw(batch)));
	}

	public void toggleDrawTiles() {
		drawTiles = !drawTiles;
	}

	public void toggleDrawEntities() {
		drawEntities = !drawEntities;
	}

	public void toggleDrawShadows() {
		drawShadows = !drawShadows;
	}

	public void toggleDrawLights() {
		drawLights = !drawLights;
	}

	@Override
	public void dispose() {

	}
}
