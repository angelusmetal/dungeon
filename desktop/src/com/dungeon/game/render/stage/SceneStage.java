package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.*;
import com.dungeon.engine.render.light.Light2;
import com.dungeon.engine.render.light.LightRenderer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;

import java.util.ArrayList;
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
	/** Holds the currently rendered normal maps */
	private final ViewPortBuffer normalMapBuffer;
	// Color to "emulate" a solid, flat normal map texture
	private final Color normalColor = Color.valueOf("8080ffff");
	// Color to "pass-through" an actual normal map texture
	private final Color textureColor = Color.valueOf("00000000");
	private boolean usingNormalTexture = true;

	private final BlendFunctionContext addLights;
	private final BlendFunctionContext blendLights;
	private final BlendFunctionContext blendSprites;
	private final Sprite shadow;

	private static final float SHADOW_INTENSITY = 0.6f;
	private final Color shadowColor = new Color(0, 0, 0, SHADOW_INTENSITY);
	private final Color lightColor = Color.WHITE.cpy();
	private static final float MAX_HEIGHT_ATTENUATION = 100;
	private static final int VERTICAL_OFFSET = -2;

	private boolean drawTiles = true;
	private boolean drawEntities = true;
	private boolean drawShadows = true;
	private boolean drawLights = true;
	private boolean occludeTiles = true;
	// For debugging; to see the resulting normal map buffer
	private boolean showNormalMapsOnly = false;

	private float renderMargin = 100f;
	private int renderMarginTiles = 3;

	// These are for rendering the tiles
	private int tSize;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int wallY;

	// These are the elements that will be rendered
	private List<Entity> entitiesToRender;
	private List<Light2> lightsToRender;

	public static int lightCount;

	private final LightRenderer lightRenderer;

	public SceneStage(ViewPort viewPort, ViewPortBuffer output) {
		this.viewPort = viewPort;
		// Output buffer
		this.output = output;
		// Base (unlit) buffer
		this.unlit = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		this.unlit.reset();
		this.unlit.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		// Lightmap buffer
		this.lights = new ViewPortBuffer(viewPort);
		this.lights.reset();
		this.lights.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		// Separate current light buffer
		this.current = new ViewPortBuffer(viewPort);
		this.current.reset();
		this.current.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		// Normal map buffer
		this.normalMapBuffer = new ViewPortBuffer(viewPort);
		this.normalMapBuffer.reset();
		this.normalMapBuffer.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		this.addLights = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE);
		this.blendLights = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.blendSprites = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.shadow = Resources.loadSprite("circle_diffuse");

		this.lightRenderer = new LightRenderer();
		lightRenderer.setAmbient(Engine.getBaseLight());
		lightRenderer.create(viewPort, normalMapBuffer.getFrameBuffer());
	}

	@Override
	public void render() {
		// Tiling variables (they keep track of tile rendering)
		tSize = Game.getEnvironment().getTilesize();
		minX = Math.max(0, viewPort.cameraX / tSize - renderMarginTiles);
		maxX = Math.min(Game.getLevel().getWidth() - 1, ((viewPort.cameraX + viewPort.cameraWidth) / tSize) + renderMarginTiles + 1);
		minY = Math.max(0, viewPort.cameraY / tSize - renderMarginTiles);
		maxY = Math.min(Game.getLevel().getHeight() - 1, ((viewPort.cameraY + viewPort.cameraHeight) / tSize) + renderMarginTiles);

		current.projectToViewPort();
		lights.projectToZero();

		// Entities to render, in order
		entitiesToRender = Engine.entities.inViewPort(viewPort, renderMargin)
//						.filter(viewPort::isInViewPort)
				.sorted(comp)
				.collect(Collectors.toList());
		lightsToRender = entitiesToRender.stream()
				.filter(e -> e.getLight() != null)
				.filter(viewPort::lightIsInViewPort)
				.map(entity -> mapLight(entity, entity.getLight()))
				.collect(Collectors.toList());
		lightCount = lightsToRender.size();

		lightRenderer.setUseNormalMapping(Engine.isNormalMapEnabled());
		if (Engine.isNormalMapEnabled()) {
			renderNormalMapBuffer();
		}
		if (!showNormalMapsOnly) {
			renderTiles();
			renderEntities();
			renderFlares();
		}
	}

	private void renderNormalMapBuffer() {
		// Render tiles
		if (showNormalMapsOnly) {
			normalMapBuffer.projectToViewPort();
		} else {
			normalMapBuffer.projectToCamera();
		}
		normalMapBuffer.render(batch -> drawFloorTiles(batch, Material.Layer.NORMAL));

		// Render entities
//		normalMapBuffer.projectToCamera();

		wallY = maxY + 1;
		normalMapBuffer.render(batch -> {
			batch.setShader(DungeonEntity.shader);
			for (Entity e : entitiesToRender) {
				if (e.getZIndex() == 0) {
					ensureNormalBlend(batch, true);
					drawWallTilesUntil(batch, e.getOrigin().y, Material.Layer.NORMAL);
				}
				// For entities that do not have a normal map, we'll simulate one by drawing the entity as a solid
				// color on the normal map buffer. Switching the shader color to control pass through / solid color
				// is quite expensive, so at least we'll keep track of when we need to do it
				if (e.getFrame().hasNormal()) {
					ensureNormalBlend(batch, true);
					e.drawNormalMap(batch);
				} else {
					ensureNormalBlend(batch, false);
					e.draw(batch);
				}
			}
			ensureNormalBlend(batch, true);
			drawWallTilesUntil(batch, viewPort.cameraY - tSize * renderMarginTiles, Material.Layer.NORMAL);
			batch.setShader(null);
		});

		// If on normal map debug mode, copy straight to the output buffer
		if (showNormalMapsOnly) {
			output.render(normalMapBuffer::draw);
			output.projectToViewPort();
			output.render(batch -> addLights.run(batch, () ->
					batch.setColor(Color.WHITE)
			));
		}

	}

	private void ensureNormalBlend(SpriteBatch batch, boolean useNormalTexture) {
		if (useNormalTexture != usingNormalTexture) {
			usingNormalTexture = !usingNormalTexture;
			batch.end();
			DungeonEntity.shader.begin();
			DungeonEntity.shader.setUniformf("u_color", useNormalTexture ? textureColor : normalColor);
			DungeonEntity.shader.end();
			batch.begin();
		}
	}

	private void renderTiles() {
		if (drawTiles) {
			// Draw tiles to unlit buffer
			unlit.projectToViewPort();
			unlit.render(batch -> drawFloorTiles(batch, Material.Layer.DIFFUSE));
			// Also, draw entities with zIndex < 0 (floor entities)
			unlit.render(batch -> blendSprites.run(batch, () -> {
					batch.setShader(entityShader);
					entitiesToRender.stream()
							.filter(e -> e.getZIndex() < 0)
							.forEach(e -> e.draw(batch));
			}));

		} else {
			unlit.render(this::clearBufferWhite);
		}
		if (drawLights) {
			renderLights(drawShadows);
		} else {
			lights.render(this::clearBufferWhite);
		}
		// Combine tiles with lighting
		unlit.projectToZero();
		unlit.render(batch -> blendLights.run(batch, () -> lights.draw(batch)));
		// Output lit tiles
		output.render(unlit::draw);
	}

	private void renderEntities() {
		if (drawEntities) {
			wallY = maxY + 1;
			// Draw entities to unlit buffer
			unlit.projectToViewPort();
			unlit.render(batch -> blendSprites.run(batch, () -> {
				// Clear buffer with transparent
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.setShader(entityShader);
				// Iterate entities in render order and draw them
				entitiesToRender.stream()
//						.filter(viewPort::isInViewPort)
						.filter(e -> e.getZIndex() >= 0)
						.filter(e -> !e.isSelfIlluminated())
						.forEach(e -> {
					if (e.getZIndex() == 0) {
						drawWallTilesUntil(batch, e.getOrigin().y, Material.Layer.DIFFUSE);
					}
					e.draw(batch);
				});
				drawWallTilesUntil(batch, viewPort.cameraY - tSize * renderMarginTiles, Material.Layer.DIFFUSE);
			}));

			if (drawLights) {
				renderLights(false);
			} else {
				lights.render(this::clearBufferWhite);
			}
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
		output.render(batch -> addLights.run(batch, () -> entitiesToRender.stream()
				.filter(e -> e.getFlare() != null)
				.filter(viewPort::flareIsInViewPort)
				.forEach(flare -> {
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
			if (flare.getFlare().mirror) {
				sprite.setRotation(-flare.getFlare().angle);
				sprite.draw(batch);
			}
		})));

		// Draw self-illuminated entities
		output.render(batch -> entitiesToRender.stream()
				.filter(Entity::isSelfIlluminated)
				.filter(e -> e.getZIndex() > 0)
				.forEach(e -> e.draw(batch))
		);
	}

	private void drawFloorTiles(SpriteBatch batch, Material.Layer layer) {
		// Only render the visible portion of the map
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				Sprite floor;
				if (layer == Material.Layer.DIFFUSE) {
					floor = Game.getLevel().getFloorAnimation(x, y).getKeyFrame(Engine.time(), true).getDiffuse();
				} else {
					floor = Game.getLevel().getFloorAnimation(x, y).getKeyFrame(Engine.time(), true).getNormal();
				}
				floor.setPosition(x * tSize, y * tSize);
				floor.draw(batch);
				Game.getLevel().setDiscovered(x, y);
			}
		}
	}

	private void drawWallTilesUntil(SpriteBatch batch, float pointY, Material.Layer layer) {
		int eY = (int) (pointY / tSize);
		if (wallY == eY) {
			return;
		}

		// If possible, continue with the following vertical stripes, from the top
		for (int y = wallY - 1; y >= eY; y--) {
			for (int x = minX; x < maxX; x++) {
				Sprite wall;
				if (layer == Material.Layer.DIFFUSE) {
					wall = Game.getLevel().getWallAnimation(x, y).getKeyFrame(Engine.time(), true).getDiffuse();
				} else {
					wall = Game.getLevel().getWallAnimation(x, y).getKeyFrame(Engine.time(), true).getNormal();
				}
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
		List<Float> geometry;
		if (withShadows) {
			geometry = new ArrayList<>();
			if (occludeTiles) {
				for (int x = minX; x < maxX; x++) {
					for (int y = maxY; y > minY; y--) {
						if (Game.getLevel().isSolid(x, y)) {
							geometry.add((float) x * tSize);
							geometry.add((float) y * tSize + tSize);
							geometry.add((float) x * tSize);
							geometry.add((float) y * tSize);

							geometry.add((float) x * tSize + tSize);
							geometry.add((float) y * tSize + tSize);
							geometry.add((float) x * tSize);
							geometry.add((float) y * tSize + tSize);

							geometry.add((float) x * tSize + tSize);
							geometry.add((float) y * tSize);
							geometry.add((float) x * tSize + tSize);
							geometry.add((float) y * tSize + tSize);

							geometry.add((float) x * tSize);
							geometry.add((float) y * tSize);
							geometry.add((float) x * tSize + tSize);
							geometry.add((float) y * tSize);
						}
					}
				}
			}
			entitiesToRender.stream()
					.filter(e -> e.shadowType() == ShadowType.RECTANGLE)
					.flatMap(this::mapGeometry)
					.forEach(geometry::add);
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
				entitiesToRender.stream()
						.filter(e -> e.shadowType() == ShadowType.CIRCLE || e.shadowType() == ShadowType.RECTANGLE)
						.forEach(entity -> circleShadow(entity, batch));
				blendSprites.unset(batch);
			});
		}
	}

	private void clearBufferWhite(SpriteBatch ignore) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private Stream<Float> mapGeometry(Entity entity) {
		return entity.getOcclusionSegments().stream()
				.flatMap(vec -> Stream.of(vec.x + entity.getOrigin().x, vec.y + entity.getOrigin().y));
	}

	/** Draws a circular shadow */
	private void circleShadow(Entity blocker, SpriteBatch batch) {
		// Draw shadow at the feet of the entity
		shadowColor.a = SHADOW_INTENSITY * blocker.getColor().a;
		float attenuation = 1f - Math.min(blocker.getZPos(), MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
		float width = blocker.getBody().getBoundingBox().x * attenuation;
		float height = width / 3f;

		shadow.setColor(shadowColor);
		shadow.setBounds(blocker.getOrigin().x - width / 2f, blocker.getBody().getBottomLeft().y - height / 2f + blocker.getBody().getBoundingBox().y / 6f /*+ VERTICAL_OFFSET*/, width, height);
		shadow.draw(batch);
	}

	private Light2 mapLight(Entity emitter, Light light) {
		Color color = light.color.cpy();
		// TODO get rid of light.dim
		Vector2 origin = emitter.getBody().getCenter().cpy().add(light.offset.x + light.displacement.x, 0f);
		float z = emitter.getZPos() + light.offset.y + light.displacement.y;
		float range = light.diameter / 2f;
		// This helps build a believable height effect
		// (the light scatters more, so the radius grows but the intensity dims)
		float attn = (float) Math.pow(0.5f, z / range);
		color.a *= emitter.getColor().a * light.dim * attn;
		return new Light2(origin,
				z,
				4f, // This is the physical radius of the light, not how far it reaches
				range / attn,
				color,
				light.castsShadow || Engine.isShadowCastEnforced());
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

	public void toggleNormalMapOnly() {
		showNormalMapsOnly = !showNormalMapsOnly;
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
