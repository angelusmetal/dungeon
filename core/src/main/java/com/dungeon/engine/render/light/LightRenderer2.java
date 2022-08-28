package com.dungeon.engine.render.light;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dungeon.engine.Engine;
import com.dungeon.engine.resource.Resources;

import java.util.List;

public class LightRenderer2 implements Disposable {

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
	private final float[] shadowVertexes = new float[20];

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

	private OrthographicCamera camera;
	private final Matrix4 ortho = new Matrix4();

	public void create (OrthographicCamera camera, FrameBuffer normalMap) {
		this.camera = camera;
		ortho.setToOrtho2D(0, 0, camera.viewportWidth, camera.viewportHeight);
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

		// Buffer containing all lights
		allLightsBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) camera.viewportWidth, (int) camera.viewportHeight, false);
		allLightsTexture = new TextureRegion(allLightsBuffer.getColorBufferTexture());
		allLightsTexture.flip(false, true);

		// Buffer containing the current light (this is only necessary when rendering stencil shadows)
		currentLightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) camera.viewportWidth, (int) camera.viewportHeight, false);
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

	public void render(List<RenderLight> lights, FloatArray occludingSegments) {

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

		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		allLightsBuffer.begin();
		ScreenUtils.clear(ambient.r, ambient.g, ambient.b, ambient.a);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);

		// Draw all lights that do not cast shadows directly on the all-lights buffer (cheaper)
		lightShader.bind();
		batch.setShader(lightShader);
		batch.setProjectionMatrix(ortho);
		lights.stream()
				.filter(light -> !light.castsShadows())
				.forEach(this::drawSimpleLight);
		allLightsBuffer.end();

		// Draw all lights that cast shadows in an intermediate buffer (more expensive)
		lights.stream()
				.filter(RenderLight::castsShadows)
				.forEach(light -> drawComplexLight(light, occludingSegments));

		if (renderGeometry) {
			drawGeometry(lights, occludingSegments);
		}
		batch.setBlendFunction(oldSrcFunc, oldDstFunc);
	}

	public void drawToScreen(SpriteBatch spriteBatch) {
		spriteBatch.draw(allLightsTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
	}

	public void resize(int width, int height, FrameBuffer normalMapBuffer) {
		allLightsBuffer.dispose();
		allLightsBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		allLightsTexture = new TextureRegion(allLightsBuffer.getColorBufferTexture());
		allLightsTexture.flip(false, true);

		// Buffer containing the current light (this is only necessary when rendering stencil shadows)
		currentLightBuffer.dispose();
		currentLightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		currentLightTexture = new TextureRegion(currentLightBuffer.getColorBufferTexture());
		currentLightTexture.flip(false, true);

		this.normalMapBuffer = normalMapBuffer;
		normalMapTexture = new TextureRegion(normalMapBuffer.getColorBufferTexture());
		normalMapTexture.flip(false, true);

		ortho.setToOrtho2D(0, 0, width, height);
	}

	private void drawSimpleLight(RenderLight light) {
		Vector3 origin = camera.project(light.getOrigin().cpy());
		// Draw light
		lightShader.setUniformf("u_lightRange", light.getRange() / camera.zoom);
		if (useNormalMapping) {
			lightShader.setUniformf("u_lightOrigin", origin.x, origin.y, origin.z);
			lightShader.setUniformf("u_specular", Engine.settings.getSpecular());
		} else {
			lightShader.setUniformf("u_lightOrigin", origin.x, origin.y + origin.z, 0f);
		}
		lightShader.setUniformf("u_lightColor", light.getColor());
		lightShader.setUniformf("u_lightHardness", 0.5f);
		lightShader.setUniformf("u_ambientColor", Color.BLACK);
		lightShader.setUniformf("u_decay", light.getDecay());
		batch.begin();
		batch.draw(lightTexture, 0, 0, currentLightBuffer.getWidth(), currentLightBuffer.getHeight());
		batch.end();
	}

	private void drawComplexLight(RenderLight light, FloatArray segments) {
		currentLightBuffer.begin();

		// Clear light buffer
		ScreenUtils.clear(0f, 0f, 0f, 0f);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		lightShader.bind();
		batch.setShader(lightShader);
		batch.setProjectionMatrix(ortho);
		drawSimpleLight(light);

		// Draw shadows
		batch.setProjectionMatrix(camera.combined);
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
		for (int i = 0; i < segments.size - 3; i += 4) {
			Vector2 origin2 = new Vector2(light.getOrigin().x, light.getOrigin().y);
			s1.set(segments.get(i), segments.get(i+1));
			s2.set(segments.get(i+2), segments.get(i+3));

			u1.set(s1).sub(origin2).scl(1000f).add(origin2);
			u2.set(s2).sub(origin2).scl(1000f).add(origin2);
			// Shadows are only drawn one way - this reduces a lot of artifacts
			if (orientation(s1, s2, origin2) > 0) {
				normal.set(s1).sub(origin2).nor().rotate90(1).scl(light.getRadius());
				p1.set(s1).sub(origin2).add(normal).scl(1000f).add(origin2);
				normal.set(s2).sub(origin2).nor().rotate90(1).scl(light.getRadius());
				p2.set(s2).sub(origin2).sub(normal).scl(1000f).add(origin2);
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
		currentLightBuffer.end();

		// Blend the light buffer onto the all-lights buffer
		allLightsBuffer.begin();
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		batch.setProjectionMatrix(ortho);
		batch.begin();
		batch.setShader(null);
		// Set fixed projection here
		batch.draw(currentLightTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
		batch.end();
		allLightsBuffer.end();
	}

	private void drawGeometry(List<RenderLight> lights, FloatArray segments) {
		// Draw geometry
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(segmentColor);
		for (int i = 0; i < segments.size - 3; i += 4) {
			shapeRenderer.line(segments.get(i), segments.get(i+1), segments.get(i+2), segments.get(i+3));
		}
		for (RenderLight light : lights) {
			shapeRenderer.circle(light.getOrigin().x, light.getOrigin().y, light.getRadius());
		}
		shapeRenderer.end();
	}

	/**
	 * Find orientation of ordered triplet (p, q, r)
	 * See: <a href="https://www.geeksforgeeks.org/orientation-3-ordered-points/">...</a>
	 * @return 0 if colinear, > 0 if clockwise, < 0 if counterclockwise
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