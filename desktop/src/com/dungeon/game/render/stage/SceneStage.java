package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ShadowType;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.render.light.Light2;
import com.dungeon.engine.render.light.LightRenderer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SceneStage implements Renderer {

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
	/** Output (scaled) buffer */
	private final ViewPortBuffer output;
	/** Base (unlit) buffer */
	private final ViewPortBuffer unlit;
	/** Holds the currently rendered light mask*/
	private final ViewPortBuffer lights;
	/** Holds the currently rendered light */
	private final ViewPortBuffer current;
	private final BlendFunctionContext addLights;
	private final BlendFunctionContext blendLights;
	private final BlendFunctionContext blendSprites;
	private final TextureRegion shadow;

	private static final float SHADOW_INTENSITY = 0.6f;
	private final Color shadowColor = new Color(0, 0, 0, SHADOW_INTENSITY);
	private final Color lightColor = Color.WHITE.cpy();
	private static final float MAX_HEIGHT_ATTENUATION = 100;
	private static final int VERTICAL_OFFSET = -2;

	private boolean drawTiles = true;
	private boolean drawEntities = true;
	private boolean drawShadows = true;
	private boolean drawLights = true;

	// These are for rendering the tiles
	private int tSize;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int wallY;

	public static int lightCount;

	private final FrameBuffer normalMapBuffer;
	private final LightRenderer lightRenderer;

	public SceneStage(ViewPort viewPort, ViewPortBuffer output) {
		this.viewPort = viewPort;
		// Output buffer
		this.output = output;
		// Base (unlit) buffer
		this.unlit = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		this.unlit.reset();
		// Lightmap buffer
		this.lights = new ViewPortBuffer(viewPort);
		this.lights.reset();
		this.lights.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		// Separate current light buffer
		this.current = new ViewPortBuffer(viewPort);
		this.current.reset();
		this.current.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.addLights = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE);
		this.blendLights = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.blendSprites = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.shadow = new TextureRegion(Resources.textures.get("circle_diffuse.png"));

		this.normalMapBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewPort.width, viewPort.height, false);
		this.lightRenderer = new LightRenderer();
		lightRenderer.setAmbient(Engine.getBaseLight());
		lightRenderer.create(viewPort, normalMapBuffer);
	}

	@Override
	public void render() {
		// Tiling variables (they keep track of tile rendering)
		tSize = Game.getEnvironment().getTilesize();
		minX = Math.max(0, viewPort.cameraX / tSize);
		maxX = Math.min(Game.getLevel().getWidth() - 1, (viewPort.cameraX + viewPort.cameraWidth) / tSize) + 1;
		minY = Math.max(0, viewPort.cameraY / tSize - 1);
		maxY = Math.min(Game.getLevel().getHeight() - 1, (viewPort.cameraY + viewPort.cameraHeight) / tSize);
		wallY = maxY + 1;

		current.projectToViewPort();
		lights.projectToZero();
		output.projectToZero();

		renderTiles();
		renderEntities();
		renderFlares();
	}

	private void renderTiles() {
		if (drawTiles) {
			// Draw tiles
			unlit.projectToViewPort();
			unlit.render(this::drawFloorTiles);
			// Draw entities with zIndex < 0 (floor entities)
			unlit.render(batch -> blendSprites.run(batch, () -> {
				batch.setShader(entityShader);
				Engine.entities.inViewPort(viewPort)
						.filter(viewPort::isInViewPort)
						.filter(e -> e.getZIndex() < 0)
						.sorted(comp)
						.forEach(e -> e.draw(batch, viewPort));
			}));
		} else {
			unlit.render(batch -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}
		if (drawLights) {
			renderLights(drawShadows);
		} else {
			lights.render(batch -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}
		// Combine tiles with lighting
		unlit.projectToZero();
		unlit.render(batch -> blendLights.run(batch, () -> lights.draw(batch)));
		// Output lit tiles
		output.render(unlit::draw);
	}

	private void renderEntities() {
		if (drawLights) {
			renderLights(false);
		} else {
			lights.render(batch -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}
		if (drawEntities) {
			unlit.projectToViewPort();
			unlit.render(batch -> blendSprites.run(batch, () -> {
				// Clear buffer with black
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.setShader(entityShader);
				// Iterate entities in render order and draw them
				Engine.entities.inViewPort(viewPort, 100f)
//						.filter(viewPort::isInViewPort)
						.filter(e -> e.getZIndex() >= 0)
						.sorted(comp)
						.forEach(e -> {
					if (e.getZIndex() == 0) {
						drawWallTilesUntil(batch, e.getOrigin().y);
					}
					e.draw(batch, viewPort);
				});
				drawWallTilesUntil(batch, viewPort.cameraY - tSize);
			}));
			// Combine tiles with lighting
			unlit.projectToZero();
			unlit.render(batch -> blendLights.run(batch, () -> lights.draw(batch)));
			// Output lit entities
			output.render(unlit::draw);
		}
	}

	private void renderFlares() {
		// Draw flares
		output.projectToViewPort();
		output.render(batch -> addLights.run(batch, () -> {
			Engine.entities.inViewPort(viewPort, 200f).filter(viewPort::flareIsInViewPort).filter(e -> e.getFlare() != null).forEach(flare -> {
				lightColor.set(flare.getFlare().color).premultiplyAlpha().mul(flare.getFlare().dim);
				Vector2 displacement = flare.getLight() != null ? flare.getLight().displacement : Vector2.Zero;
				Vector2 offset = flare.getFlare().offset;
				// Draw light texture
				Sprite sprite = flare.getFlare().sprite;
				sprite.setOriginCenter();
				sprite.setScale(flare.getFlare().dim);
				sprite.setRotation(flare.getFlare().angle);
				sprite.setBounds(
						offset.x + flare.getOrigin().x + displacement.x - flare.getFlare().diameter / 2f,
						offset.y + flare.getOrigin().y + displacement.y + flare.getZPos() - flare.getFlare().diameter / 2f,
						flare.getFlare().diameter,
						flare.getFlare().diameter);
				sprite.setColor(lightColor);
				sprite.draw(batch);
			});
			batch.setColor(Color.WHITE);
		}));
	}

	private void drawFloorTiles(SpriteBatch batch) {
		// Only render the visible portion of the map
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				Sprite floor = Game.getLevel().getFloorAnimation(x, y).getKeyFrame(Engine.time(), true);
				floor.setPosition(x * tSize, y * tSize);
				floor.draw(batch);
				Game.getLevel().setDiscovered(x, y);
			}
		}
	}

	private void drawWallTilesUntil(SpriteBatch batch, float pointY) {
		int eY = (int) (pointY / tSize);
		if (wallY == eY) {
			return;
		}

		// If possible, continue with the following vertical stripes, from the top
		for (int y = wallY - 1; y >= eY; y--) {
			for (int x = minX; x < maxX; x++) {
				Sprite wall = Game.getLevel().getWallAnimation(x, y).getKeyFrame(Engine.time(), true);
				// If it partially occludes a non-solid tile, it is rendered semi-transparent
				if (!Game.getLevel().isSolid(x, y + 1)) {
					wall.setColor(1, 1, 1, 0.8f);
					wall.setPosition(x * tSize, y * tSize);
					wall.draw(batch);
					wall.setColor(1, 1, 1, 1f);
				} else {
					wall.setPosition(x * tSize, y * tSize);
					wall.draw(batch);
				}
				Game.getLevel().setDiscovered(x, y);
			}
		}
		// Record the last reached coordinate
		wallY = eY;
	}

	private void renderLights(boolean withShadows) {
		lightCount = (int) Engine.entities.inViewPort(viewPort, 200f)
				.filter(e -> e.getLight() != null)
				.filter(viewPort::lightIsInViewPort)
				.count();
		List<Light2> lightsToRender = Engine.entities.inViewPort(viewPort, 200f)
				.filter(e -> e.getLight() != null)
				.filter(viewPort::lightIsInViewPort)
				.map(entity -> mapLight(entity, entity.getLight()))
				.collect(Collectors.toList());
		List<Float> geometry;
		if (withShadows) {
			 geometry = Engine.entities.inViewPort(viewPort, 100f)
					.filter(e -> e.shadowType() == ShadowType.RECTANGLE)
					.flatMap(this::mapGeometry)
					.collect(Collectors.toList());
		} else {
			geometry = Collections.emptyList();
		}

		lightRenderer.render(lightsToRender, geometry);
		lights.projectToZero();
		lights.render(batch -> lightRenderer.drawToCamera());
		if (withShadows) {
			lights.projectToViewPort();
			lights.render(batch -> {
				blendSprites.set(batch);
				Engine.entities.inViewPort(viewPort, 100f)
						.filter(e -> e.shadowType() == ShadowType.CIRCLE || e.shadowType() == ShadowType.RECTANGLE)
						.forEach(entity -> circleShadow(entity, batch));
				blendSprites.unset(batch);
			});
		}
	}

	private Stream<Float> mapGeometry(Entity entity) {
		return entity.getOcclusionSegments().stream()
				.flatMap(vec -> Stream.of(vec.x + entity.getOrigin().x, vec.y + entity.getOrigin().y));
	}

	/** Draws a circular shadow */
	private void circleShadow(Entity blocker, SpriteBatch batch) {
		// Draw shadow at the feet of the entity
		shadowColor.a = SHADOW_INTENSITY * blocker.getColor().a;
		batch.setColor(shadowColor);
		float attenuation = 1 - Math.min(blocker.getZPos(), MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
		float width = blocker.getBody().getBoundingBox().x * attenuation;
		float height = width / 3 * attenuation;

		batch.draw(shadow, blocker.getBody().getBottomLeft().x, blocker.getBody().getBottomLeft().y + VERTICAL_OFFSET, width, height);

//		// Draw projected shadow
//		// TODO double check whether we still need origin & zpos here
//		Vector2 o = blocker.getOrigin().cpy().sub(light.getOrigin()).sub(0, light.getZPos()).sub(offset);
//		float shadowLen = o.len();
//		if (shadowLen < 2) {
//			return;
//		}
//		shadowColor.a = SHADOW_INTENSITY * Util.clamp(1 - shadowLen / 100f) * blocker.getColor().a;
//		batch.setColor(shadowColor);
//		batch.draw(
//				shadow,
//				blocker.getOrigin().x,
//				blocker.getOrigin().y /*- width / 2*/,
//				0,
//				width / 2,
//				width,
//				width,
//				shadowLen / 10f,
//				1 + shadowLen / 100f,
//				o.angle(),
//				true);
	}

	private Light2 mapLight(Entity emitter, Light light) {
		Color color = light.color.cpy();
		// TODO get rid of light.dim
		color.a *= emitter.getColor().a * light.dim;
		return new Light2(emitter.getOrigin().cpy().add(light.displacement).add(light.offset).add(0, emitter.getZPos()),
				4f,
				light.diameter / 2f,
				color,
				light.castsShadow);
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
		output.dispose();
		unlit.dispose();
		lights.dispose();
		current.dispose();
		normalMapBuffer.dispose();
	}
}
