package com.dungeon.engine.render.light;

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
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.Engine;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.viewport.ViewPort;

import java.util.List;

public class LightRenderer implements Disposable {

	// Shaders
	private ShaderProgram normalMapShader;
	private ShaderProgram simpleLightShader;
	private ShaderProgram shadowShader;

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;

	// Light buffer & texture
	private FrameBuffer currentLightBuffer;
	private TextureRegion currentLightTexture;
	private FrameBuffer allLightsBuffer;
	private TextureRegion allLightsTexture;
	private TextureRegion simpleLightTexture;

	// Normal map buffer & texture
	private FrameBuffer normalMapBuffer;
	private TextureRegion normalMapTexture;

	// Single-pixel texture for drawing into the buffer
	private Texture flatMapTexture;

	// Used for drawing shadow triangles
	private float[] shadowVertexes = new float[20];

	private final float umbraColor = new Color(0, 0, 0, 1).toFloatBits();
	private final float penumbraColor = new Color(0, 0, 0, 0).toFloatBits();

	// Not a copy but a reference to the actual color; so if it is changed it gets reflected during rendering
	private Color ambient = Color.BLACK;
	private final Color segmentColor = Color.RED;

	// Render geometry
	private boolean renderGeometry = false;

	// Use normal mapping
	private boolean useNormalMapping = false;

	// Current light shader being used
	private ShaderProgram lightShader;
	// Current light texture region being used
	private TextureRegion lightTexture;

	private ViewPort viewPort;

	public void create (ViewPort viewPort, FrameBuffer normalMap) {
		this.viewPort = viewPort;
		// Single-pixel texture for drawing triangles
		Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		p.drawPixel(0, 0, Color.rgba8888(1f, 1f, 1f,1f));
		flatMapTexture = new Texture(p);

		// Normal map buffer, texture and shader
		normalMapBuffer = normalMap;
		normalMapTexture = new TextureRegion(normalMap.getColorBufferTexture());
		normalMapTexture.flip(false, true);
		normalMapShader = Resources.shaders.get("df_vertex.glsl|light/normal_map.glsl");

		// Simple light shader
		simpleLightShader = Resources.shaders.get("df_vertex.glsl|light/simple.glsl");
		simpleLightTexture = new TextureRegion(flatMapTexture);

		// Shadow shader
		shadowShader = Resources.shaders.get("df_vertex.glsl|light/penumbra.glsl");

		// Light buffer, texture and shader (for rendering penumbra)
		allLightsBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewPort.width, viewPort.height, false);
		allLightsTexture = new TextureRegion(allLightsBuffer.getColorBufferTexture());
		allLightsTexture.flip(false, true);
		currentLightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewPort.width, viewPort.height, false);
		currentLightTexture = new TextureRegion(currentLightBuffer.getColorBufferTexture());
		currentLightTexture.flip(false, true);

		// Sprite batch for drawing lights & shadows
		batch = new SpriteBatch();

		// Shape renderer for drawing geometry and light contours
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
		currentLightBuffer.dispose();
		allLightsBuffer.dispose();
	}

	public void render(List<Light2> lights, List<Float> occludingSegments) {

		// Clear main buffer
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		int oldSrcFunc = batch.getBlendSrcFunc();
		int oldDstFunc = batch.getBlendDstFunc();

		// Pick up the shader and texture region for rendering, based on whether normal mapping is enabled
		if (useNormalMapping) {
			lightShader = normalMapShader;
			lightTexture = normalMapTexture;
		} else {
			lightShader = simpleLightShader;
			lightTexture = simpleLightTexture;
		}

		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width, viewPort.height);
		shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.cameraWidth, viewPort.cameraHeight);

		// Draw all lights that do not cast shadows directly on the all-lights buffer (cheaper)
		allLightsBuffer.begin();
		Gdx.gl.glClearColor(ambient.r, ambient.g, ambient.b, ambient.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		lights.stream()
				.filter(light -> !light.castsShadows())
				.forEach(this::drawSimpleLight);
		allLightsBuffer.end();

		// Draw all lights that cast shadows in a separate buffer (more expensive)
		lights.stream()
				.filter(Light2::castsShadows)
				.forEach(light -> drawLight(light, occludingSegments));

		if (renderGeometry) {
			drawGeometry(lights, occludingSegments);
		}
		batch.setBlendFunction(oldSrcFunc, oldDstFunc);
	}

	public void drawToScreen() {
		// Draw the resulting light buffer onto the screen
		batch.begin();
		batch.setShader(null);
		batch.draw(allLightsTexture, 0, 0, viewPort.width, viewPort.height);
		batch.end();
	}

	public void drawToCamera() {
		// Draw the resulting light buffer onto the screen
		batch.begin();
		batch.setShader(null);
		batch.draw(allLightsTexture, 0, 0, viewPort.cameraWidth, viewPort.cameraHeight);
		batch.end();
	}

	private void drawSimpleLight(Light2 light) {
		Vector2 origin = light.getOrigin().cpy().sub(viewPort.cameraX, viewPort.cameraY).scl(viewPort.getScale());
		// Draw light
		lightShader.begin();
		lightShader.setUniformf("u_lightRange", light.getRange() * viewPort.getScale());
		if (useNormalMapping) {
			lightShader.setUniformf("u_lightOrigin", origin.x, origin.y, light.getZ());
			lightShader.setUniformf("u_specular", Engine.settings.getSpecular());
		} else {
			lightShader.setUniformf("u_lightOrigin", origin.x, origin.y + light.getZ(), 0f);
		}
		lightShader.setUniformf("u_lightColor", light.getColor());
		lightShader.setUniformf("u_lightHardness", 0.5f);
		lightShader.setUniformf("u_ambientColor", Color.BLACK);
		lightShader.end();
		batch.setShader(lightShader);
		batch.begin();
		batch.draw(lightTexture, 0, 0, currentLightBuffer.getWidth(), currentLightBuffer.getHeight());
		batch.end();
	}

	private void drawLight(Light2 light, List<Float> segments) {
		currentLightBuffer.begin();

		// Clear light buffer
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		drawSimpleLight(light);

		// Draw shadows
		batch.getProjectionMatrix().setToOrtho2D(viewPort.cameraX, viewPort.cameraY, viewPort.cameraWidth, viewPort.cameraHeight);
		batch.begin();
		batch.setShader(shadowShader);
		// Draw each shadow
		Vector2 normal = new Vector2();
		Vector2 s1 = new Vector2();
		Vector2 s2 = new Vector2();
		Vector2 p1 = new Vector2();
		Vector2 p2 = new Vector2();
		Vector2 u1 = new Vector2();
		Vector2 u2 = new Vector2();
		for (int i = 0; i < segments.size() - 3; i += 4) {
			s1.set(segments.get(i), segments.get(i+1));
			s2.set(segments.get(i+2), segments.get(i+3));

			u1.set(s1).sub(light.getOrigin()).scl(1000f).add(light.getOrigin());
			u2.set(s2).sub(light.getOrigin()).scl(1000f).add(light.getOrigin());
			// Shadows are only drawn one way - this reduces a lot of artifacts
			if (orientation(s1, s2, light.getOrigin()) > 0) {
				normal.set(s1).sub(light.getOrigin()).nor().rotate90(1).scl(light.getRadius());
				p1.set(s1).sub(light.getOrigin()).add(normal).scl(1000f).add(light.getOrigin());
				normal.set(s2).sub(light.getOrigin()).nor().rotate90(1).scl(light.getRadius());
				p2.set(s2).sub(light.getOrigin()).sub(normal).scl(1000f).add(light.getOrigin());
				shadowTriangle(p1.x, p1.y, s1.x, s1.y, u1.x, u1.y, penumbraColor, shadowVertexes);
				batch.draw(flatMapTexture, shadowVertexes, 0, shadowVertexes.length);

				shadowTriangle(p2.x, p2.y, s2.x, s2.y, u2.x, u2.y, penumbraColor, shadowVertexes);
				batch.draw(flatMapTexture, shadowVertexes, 0, shadowVertexes.length);

				shadowTriangle(u1.x, u1.y, s1.x, s1.y, s2.x, s2.y, umbraColor, shadowVertexes);
				batch.draw(flatMapTexture, shadowVertexes, 0, shadowVertexes.length);
				shadowTriangle(u2.x, u2.y, s2.x, s2.y, u1.x, u1.y, umbraColor, shadowVertexes);
				batch.draw(flatMapTexture, shadowVertexes, 0, shadowVertexes.length);
			}
		}
		batch.end();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width, viewPort.height);
		currentLightBuffer.end();

		// Blend the light buffer onto the all-lights buffer
		allLightsBuffer.begin();
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		batch.begin();
		batch.setShader(null);
		batch.draw(currentLightTexture, 0, 0, viewPort.width, viewPort.height);
		batch.end();
		allLightsBuffer.end();
	}

	private void drawGeometry(List<Light2> lights, List<Float> segments) {
		// Draw geometry
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(segmentColor);
		for (int i = 0; i < segments.size() - 3; i += 4) {
			shapeRenderer.line(segments.get(i), segments.get(i+1), segments.get(i+2), segments.get(i+3));
		}
		for (Light2 light : lights) {
			shapeRenderer.circle(light.getOrigin().x, light.getOrigin().y, light.getRadius());
		}
		shapeRenderer.end();
	}

	/**
	 * Find orientation of ordered triplet (p, q, r)
	 * See: https://www.geeksforgeeks.org/orientation-3-ordered-points/
	 * @return 0 if colinear, > 0 if clockwise, < 0 if counter clockwise
	 */
	float orientation(Vector2 p, Vector2 q, Vector2 r) {
		return  (q.y - p.y) * (r.x - q.x) -
				(q.x - p.x) * (r.y - q.y);
	}

	/**
	 * Create a triangle for shadow (either umbra or penumbra)
	 */
	private void shadowTriangle(float x, float y, float x2, float y2, float x3, float y3, float color, float[] vertexes) {
		vertexes[0] = x;
		vertexes[1] = y;
		vertexes[2] = color;
		vertexes[3] = 0f;
		vertexes[4] = 0f;
		vertexes[5] = x2;
		vertexes[6] = y2;
		vertexes[7] = color;
		vertexes[8] = 0f;
		vertexes[9] = 1f;
		vertexes[10] = x3;
		vertexes[11] = y3;
		vertexes[12] = color;
		vertexes[13] = 1f;
		vertexes[14] = 0f;
		vertexes[15] = x;
		vertexes[16] = y;
		vertexes[17] = color;
		vertexes[18] = 1f;
		vertexes[19] = 1f;
	}

	public boolean isRenderGeometry() {
		return renderGeometry;
	}

	public void setRenderGeometry(boolean renderGeometry) {
		this.renderGeometry = renderGeometry;
	}

	public Color getAmbient() {
		return ambient;
	}

	/**
	 * Wires (not copies) the provided color to the renderer as ambient color. Changes to the original
	 * object will get picked up during the next rendering cycle.
	 */
	public void setAmbient(Color ambient) {
		this.ambient = ambient;
	}

	public Color getSegmentColor() {
		return segmentColor;
	}

	public void setUseNormalMapping(boolean useNormalMapping) {
		this.useNormalMapping = useNormalMapping;
	}
}
