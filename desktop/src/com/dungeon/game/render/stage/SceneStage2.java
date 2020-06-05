package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class SceneStage2 implements Renderer {

	// Entities need to be rendered with premultiplied alpha onto the alpha-enabled buffer
	private static ShaderProgram entityShader = Resources.shaders.get("df_vertex.glsl|premultiplied_alpha_fragment.glsl");
//	private static ShaderProgram colorShader = Resources.shaders.get("df_vertex.glsl|solid_color_fragment.glsl");
	public static final float SHADOW_PROJECT_DISTANCE = 2_000_000f;

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

	@FunctionalInterface
	private interface ShadowRenderer {
		void renderShadow(Entity light, Entity blocker, ViewPort viewPort, SpriteBatch batch, Vector2 offset);
	}
	private final EnumMap<ShadowType, ShadowRenderer> shadowRenderer;
	private final ShapeRenderer shapeRenderer;
	private final FrameBuffer normalMapBuffer;
	private final LightRenderer lightRenderer;

	public SceneStage2(ViewPort viewPort, ViewPortBuffer output) {
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
		this.shadowRenderer = new EnumMap<>(ShadowType.class);
		this.shadowRenderer.put(ShadowType.NONE, (a, b, c, d, e) -> {});
		this.shadowRenderer.put(ShadowType.CIRCLE, this::circleShadow);
		this.shadowRenderer.put(ShadowType.RECTANGLE, this::rectangleShadow);
		this.shapeRenderer = new ShapeRenderer();
		this.shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width, viewPort.height);

		this.normalMapBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewPort.width, viewPort.height, false);
		this.lightRenderer = new LightRenderer();
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
		shapeRenderer.setProjectionMatrix(shapeRenderer.getProjectionMatrix().setToOrtho2D(viewPort.cameraX, viewPort.cameraY, viewPort.width, viewPort.height));
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
		renderLights2(drawShadows);
		// Combine tiles with lighting
		unlit.projectToZero();
		unlit.render(batch -> blendLights.run(batch, () -> lights.draw(batch)));
		// Output lit tiles
		output.render(unlit::draw);
	}

	private void renderEntities() {
		renderLights2(false);
		if (drawEntities) {
			unlit.projectToViewPort();
			unlit.render(batch -> blendSprites.run(batch, () -> {
				// Clear buffer with black
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.setShader(entityShader);
				// Iterate entities in render order and draw them
				Engine.entities.inViewPort(viewPort)
						.filter(viewPort::isInViewPort)
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
			Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::flareIsInViewPort).filter(e -> e.getFlare() != null).forEach(flare -> {
				lightColor.set(flare.getFlare().color).premultiplyAlpha().mul(flare.getFlare().dim);
				batch.setColor(lightColor);
				Vector2 displacement = flare.getLight() != null ? flare.getLight().displacement : Vector2.Zero;
				// Draw light texture
				viewPort.draw(batch,
						flare.getFlare().texture,
						flare.getOrigin().x + displacement.x,
						flare.getOrigin().y + displacement.y + flare.getZPos(),
						flare.getFlare().diameter * flare.getFlare().dim,
						flare.getFlare().angle);
			});
			batch.setColor(Color.WHITE);
		}));
	}

	private void drawFloorTiles(SpriteBatch batch) {
		// Only render the visible portion of the map
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				TextureRegion textureRegion = Game.getLevel().getFloorAnimation(x, y).getKeyFrame(Engine.time(), true);
				batch.draw(textureRegion, x * tSize, y * tSize, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
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
				TextureRegion textureRegion = Game.getLevel().getWallAnimation(x, y).getKeyFrame(Engine.time(), true);
				// If it partially occludes a non-solid tile, it is rendered semi-transparent
				if (!Game.getLevel().isSolid(x, y + 1)) {
					batch.setColor(1, 1, 1, 0.8f);
					batch.draw(textureRegion, x * tSize, y * tSize, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
					batch.setColor(1, 1, 1, 1);
				} else {
					batch.draw(textureRegion, x * tSize, y * tSize, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
				}
				Game.getLevel().setDiscovered(x, y);
			}
		}
		// Record the last reached coordinate
		wallY = eY;
	}

	private void renderLights2(boolean withShadows) {
		lightCount = (int) Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::lightIsInViewPort).count();
		List<Light2> lightsToRender = Engine.entities.inViewPort(viewPort, 100f)
				.filter(viewPort::lightIsInViewPort)
				.map(entity -> mapLight(entity, entity.getLight()))
				.collect(Collectors.toList());
		List<Float> geometry;
		if (withShadows) {
			 geometry = Engine.entities.inViewPort(viewPort, 100f)
					.filter(e -> e.shadowType() == ShadowType.RECTANGLE)
					.flatMap(entity -> mapGeometry(entity).stream())
					.collect(Collectors.toList());
		} else {
			geometry = Collections.emptyList();
		}

		lightRenderer.render(lightsToRender, geometry);
		lights.projectToZero();
		lights.render(batch -> lightRenderer.drawToCamera());
	}

	private List<Float> mapGeometry(Entity entity) {
		List<Float> vertexes = new ArrayList<>(16);
		vertexes.add(entity.getBody().getBottomLeft().x);
		vertexes.add(entity.getBody().getBottomLeft().y);
		vertexes.add(entity.getBody().getTopRight().x);
		vertexes.add(entity.getBody().getBottomLeft().y);

		vertexes.add(entity.getBody().getTopRight().x);
		vertexes.add(entity.getBody().getBottomLeft().y);
		vertexes.add(entity.getBody().getTopRight().x);
		vertexes.add(entity.getBody().getTopRight().y);

		vertexes.add(entity.getBody().getTopRight().x);
		vertexes.add(entity.getBody().getTopRight().y);
		vertexes.add(entity.getBody().getBottomLeft().x);
		vertexes.add(entity.getBody().getTopRight().y);

		vertexes.add(entity.getBody().getBottomLeft().x);
		vertexes.add(entity.getBody().getTopRight().y);
		vertexes.add(entity.getBody().getBottomLeft().x);
		vertexes.add(entity.getBody().getBottomLeft().y);
		return vertexes;
	}

	private void renderLights(boolean withShadows) {
		lightCount = (int) Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::lightIsInViewPort).count();
		if (drawLights) {
			if (withShadows) {
				// Clear buffer with ambient light
				lights.render(batch -> {
					Gdx.gl.glClearColor(Engine.getBaseLight().r, Engine.getBaseLight().g, Engine.getBaseLight().b, 1f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					batch.setColor(Color.WHITE);
				});
				Engine.entities.inViewPort(viewPort, 100f)
						.filter(viewPort::lightIsInViewPort)
						.forEach(e -> {
							// Each light must be rendered on a separate buffer (so it can be stenciled with projected shadows)
							current.render(batch -> {
								// Clear the buffer (black)
								Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
								Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
								drawLight(batch, e, viewPort, withShadows);
							});
							// And then merged into the lights buffer
							lights.render(batch -> addLights.run(batch, () -> current.draw(batch)));
						});
			} else {
				// If shadows are disabled, all lights are rendered directly on the light buffer
				lights.projectToViewPort();
				// Clear buffer with ambient light
				lights.render(batch -> addLights.run(batch, () -> {
					Gdx.gl.glClearColor(Engine.getBaseLight().r, Engine.getBaseLight().g, Engine.getBaseLight().b, 1f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					batch.setColor(Color.WHITE);
					Engine.entities.inViewPort(viewPort, 100f)
							.filter(viewPort::lightIsInViewPort)
							.forEach(e -> drawLight(batch, e, viewPort, withShadows));
				}));
				lights.projectToZero();
			}
		} else {
			lights.render(batch -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}
	}

	private void drawLight(SpriteBatch batch, Entity light, ViewPort viewPort, boolean drawShadows) {
		// Set light color
		lightColor.set(light.getLight().color).premultiplyAlpha().mul(light.getLight().dim);
		batch.setColor(lightColor);
		Vector2 displacement = light.getLight() != null ? light.getLight().displacement : Vector2.Zero;
		// Draw light texture
		viewPort.draw(
				batch,
				light.getLight().texture,
				light.getOrigin().x + displacement.x,
				light.getOrigin().y + displacement.y + light.getZPos(),
				light.getLight().diameter * light.getLight().dim,
				light.getLight().angle);
		// Draw shadows
		if (drawShadows) {
			// TODO double check whether we still need origin & zpos here
			// Cast shadows from entities
			Vector2 origin = light.getOrigin().cpy().add(0, light.getZPos()).add(displacement);
			Engine.entities
					.radius(origin, light.getLight().diameter / 2)
					.filter(e -> e.shadowType() != ShadowType.NONE)
					.forEach(blocker -> shadowRenderer.get(blocker.shadowType()).renderShadow(light, blocker, viewPort, batch, displacement));
			// Cast shadows from solid tiles
			int rightTile = (int) (light.getOrigin().x + light.getLight().diameter / 2) / tSize;
			int topTile = (int) (light.getOrigin().y + light.getLight().diameter / 2) / tSize;
			int tiles = (int) (light.getLight().diameter / tSize) + 1;
			for (int tx = rightTile - tiles; tx < rightTile; ++tx) {
				for (int ty = topTile - tiles; ty < topTile; ++ty) {
					if (Game.getLevel().isSolid(tx, ty)) {
						rectangleShadow(light, viewPort, batch, tx * tSize, (tx + 1) * tSize, (ty + 1) * tSize, ty * tSize, 0, shadowColor);
					}
				}
			}
		}
	}

	/** Draws a circular shadow */
	private void circleShadow(Entity light, Entity blocker, ViewPort viewPort, SpriteBatch batch, Vector2 offset) {
		// Draw shadow at the feet of the entity
		shadowColor.a = SHADOW_INTENSITY * blocker.getColor().a;
		batch.setColor(shadowColor);
		float attenuation = 1 - Math.min(blocker.getZPos(), MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
		float width = blocker.getBody().getBoundingBox().x * attenuation;
		float height = width / 3 * attenuation;

		batch.draw(shadow, blocker.getBody().getBottomLeft().x, blocker.getBody().getBottomLeft().y + VERTICAL_OFFSET, width, height);

		// Draw projected shadow
		// TODO double check whether we still need origin & zpos here
		Vector2 o = blocker.getOrigin().cpy().sub(light.getOrigin()).sub(0, light.getZPos()).sub(offset);
		float shadowLen = o.len();
		if (shadowLen < 2) {
			return;
		}
		shadowColor.a = SHADOW_INTENSITY * Util.clamp(1 - shadowLen / 100f) * blocker.getColor().a;
		batch.setColor(shadowColor);
		batch.draw(
				shadow,
				blocker.getOrigin().x,
				blocker.getOrigin().y /*- width / 2*/,
				0,
				width / 2,
				width,
				width,
				shadowLen / 10f,
				1 + shadowLen / 100f,
				o.angle(),
				true);
	}

	private void rectangleShadow(Entity light, Entity blocker, ViewPort viewPort, SpriteBatch batch, Vector2 offset) {
		shadowColor.a = SHADOW_INTENSITY * blocker.getColor().a;
		rectangleShadow(light, viewPort, batch,
				blocker.getBody().getBottomLeft().x,
				blocker.getBody().getTopRight().x,
				blocker.getBody().getTopRight().y,
				blocker.getBody().getBottomLeft().y,
				blocker.getZPos(),
				shadowColor);
	}

	private void rectangleShadow(Entity light, ViewPort viewPort, SpriteBatch batch, float left, float right, float top, float bottom, float z, Color color) {
		//shadowColor.a = SHADOW_INTENSITY * intensity;
		// Draw shadow at the feet of the entity
		batch.setColor(color);
		float attenuation = 1 - Math.min(z, MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
		float width = (right - left) * attenuation;
		float height = width / 3 * attenuation;

		batch.draw(shadow, left, bottom + VERTICAL_OFFSET, width, height);
		batch.end();

		// Draw entity in shadow
//		batch.end();
//		colorShader.begin();
//		colorShader.setUniformf("u_color", shadowColor);
//		colorShader.end();
//		ShaderProgram otherShader = batch.getShader();
//		batch.setShader(colorShader);
//		batch.begin();
//		viewPort.drawEntity(batch, blocker);
//		batch.end();
//		batch.setShader(otherShader);
//		batch.begin();

		// This is needed so that alpha channel is properly taken into account when blending projected shadows
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		Vector2 project = new Vector2();
		// Using the center instead of the origin, to make sure displacement never moves it outside the bounding box
		Vector2 origin = light.getBody().getCenter().cpy().add(light.getLight().displacement);
		if (origin.x < left) {
			// Light's left
			if (origin.y < bottom) {
				// Light's below
				project.set(left, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x3 = project.x;
				float y3 = project.y;
				project.set(right, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x5 = project.x;
				float y5 = project.y;
				drawTriangles(color, left, top, left, bottom, x3, y3, right, bottom, x5, y5);
			} else if (origin.y > top) {
				// Light's above
				project.set(left, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x3 = project.x;
				float y3 = project.y;
				project.set(right, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x5 = project.x;
				float y5 = project.y;
				drawTriangles(color, left, bottom, left, top, x3, y3, right, top, x5, y5);
			} else {
				// Light's within
				project.set(left, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x2 = project.x;
				float y2 = project.y;
				project.set(left, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x4 = project.x;
				float y4 = project.y;
				drawTriangles(color, left, top, x2, y2, left, bottom, x4, y4);
			}
		} else if (origin.x > right) {
			// Light's left
			if (origin.y < bottom) {
				// Light's below
				project.set(right, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x2 = project.x;
				float y2 = project.y;
				project.set(left, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x4 = project.x;
				float y4 = project.y;
				drawTriangles(color, right, top, x2, y2, right, bottom, x4, y4, left, bottom);
			} else if (origin.y > top) {
				// Light's above
				project.set(right, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x2 = project.x;
				float y2 = project.y;
				project.set(left, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x4 = project.x;
				float y4 = project.y;
				drawTriangles(color, right, bottom, x2, y2, right, top, x4, y4, left, top);
			} else {
				// Light's within
				project.set(right, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x2 = project.x;
				float y2 = project.y;
				project.set(right, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x4 = project.x;
				float y4 = project.y;
				drawTriangles(color, right, top, x2, y2, right, bottom, x4, y4);
			}
		} else {
			// Light's within
			if (origin.y < bottom) {
				// Light's below
				project.set(left, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x2 = project.x;
				float y2 = project.y;
				project.set(right, bottom).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x4 = project.x;
				float y4 = project.y;
				drawTriangles(color, left, bottom, x2, y2, right, bottom, x4, y4);
			} else if (origin.y > top) {
				// Light's above
				project.set(left, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x2 = project.x;
				float y2 = project.y;
				project.set(right, top).sub(origin).setLength(SHADOW_PROJECT_DISTANCE).add(origin);
				float x4 = project.x;
				float y4 = project.y;
				drawTriangles(color, left, top, x2, y2, right, top, x4, y4);
			} else {
				// Light's within
				// No shadows, for now
			}
		}
		batch.begin();
	}

	private void drawTriangles(Color color, float... shadowVertexes) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(color);
		float x1, x2, x3, y1, y2, y3;
		for (int i = 0; i < shadowVertexes.length - 5; i += 2) {
			x1 = shadowVertexes[i];
			y1 = shadowVertexes[i+1];
			x2 = shadowVertexes[i+2];
			y2 = shadowVertexes[i+3];
			x3 = shadowVertexes[i+4];
			y3 = shadowVertexes[i+5];
			shapeRenderer.triangle(x1, y1, x2, y2, x3, y3);
		}
		shapeRenderer.end();
	}

	private Light2 mapLight(Entity emitter, Light light) {
		Color color = light.color.cpy();
		color.a *= emitter.getColor().a;
		return new Light2(emitter.getOrigin().cpy().add(light.displacement).add(0, emitter.getZPos()),
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
