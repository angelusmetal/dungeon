package com.dungeon.desktop.shader;

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
import com.dungeon.game.resource.Resources;

import java.util.List;

public class LightRenderer implements Disposable {

	// Shaders
	private ShaderProgram normalMapShader;
	private ShaderProgram shadowShader;

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;

	// Light buffer & texture
	private FrameBuffer lightBuffer;
	private TextureRegion shadowTexture;

	// Normal map buffer & texure
	private FrameBuffer normalMapBuffer;
	private TextureRegion normalMapTexture;

	// Single-pixel texture for drawing into the buffer
	private Texture pixel;

	// Used for drawing shadow triangles
	private float[] shadowVertexes = new float[20];

	private final float umbraColor = new Color(0, 0, 0, 1).toFloatBits();
	private final float penumbraColor = new Color(0, 0, 0, 0).toFloatBits();
	private final Color ambient = Color.BLACK;
	private final Color segmentColor = Color.RED;

	// Render geometry
	private boolean renderGeometry = false;

	public void create (int bufferWidth, int bufferHeight, FrameBuffer normalMap) {
		// Normal map buffer, texture and shader
		normalMapBuffer = normalMap;
		normalMapTexture = new TextureRegion(normalMap.getColorBufferTexture());
		normalMapTexture.flip(false, true);
		normalMapShader = Resources.shaders.get("df_vertex.glsl|test/normal_mapping.glsl");

		// Light buffer, texture and shader (for rendering penumbra)
		lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, bufferWidth, bufferHeight, false);
		shadowShader = Resources.shaders.get("df_vertex.glsl|test/penumbra.glsl");
		shadowTexture = new TextureRegion(lightBuffer.getColorBufferTexture());
		shadowTexture.flip(false, true);

		// Single-pixel texture for drawing triangles
		pixel = new Texture("core/assets/fill.png");

		// Sprite batch for drawing lights & shadows
		batch = new SpriteBatch();

		// Shape renderer for drawing geometry and ligth contours
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, bufferWidth, bufferHeight);
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
		normalMapBuffer.dispose();
		pixel.dispose();
		lightBuffer.dispose();
	}

	public void render(List<SimpleShadowCastTest.Light> lights, List<Float> segments) {

		// Clear main buffer
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		int oldSrcFunc = batch.getBlendSrcFunc();
		int oldDstFunc = batch.getBlendDstFunc();

		for (SimpleShadowCastTest.Light light : lights) {
			drawLight(light, segments);
		}
		batch.setBlendFunction(oldSrcFunc, oldDstFunc);

		if (renderGeometry) {
			drawGeometry(lights, segments);
		}

	}

	private void drawLight(SimpleShadowCastTest.Light light, List<Float> segments) {
		lightBuffer.begin();

		// Clear light buffer
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw light with normal mapping
		normalMapShader.begin();
		normalMapShader.setUniformf("u_lightRange", light.range);// * (1 + 0.1f * MathUtils.sin(simpleShadowCastTest.time * 30)));
		normalMapShader.setUniformf("u_lightOrigin", light.origin.x, light.origin.y, 50f);
		normalMapShader.setUniformf("u_lightColor", light.color);
		normalMapShader.setUniformf("u_lightHardness", 1.0f);
		normalMapShader.setUniformf("u_ambientColor", ambient);
		normalMapShader.end();
		batch.setShader(normalMapShader);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
//		batch.setColor(Color.argb8888(0.1f, 0.3f, 0.5f, 1.0f));
		batch.draw(normalMapTexture, 0, 0, lightBuffer.getWidth(), lightBuffer.getHeight());
		batch.end();

//		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw shadows
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

			u1.set(s1).sub(light.origin).scl(1000f).add(light.origin);
			u2.set(s2).sub(light.origin).scl(1000f).add(light.origin);
			// Shadows are only drawn one way - this reduces a lot of artifacts
			if (orientation(s1, s2, light.origin) > 0) {
				normal.set(s1).sub(light.origin).nor().rotate90(1).scl(light.radius);
				p1.set(s1).sub(light.origin).add(normal).scl(1000f).add(light.origin);
				normal.set(s2).sub(light.origin).nor().rotate90(1).scl(light.radius);
				p2.set(s2).sub(light.origin).sub(normal).scl(1000f).add(light.origin);
				shadowTriangle(p1.x, p1.y, s1.x, s1.y, u1.x, u1.y, penumbraColor, shadowVertexes);
				batch.draw(pixel, shadowVertexes, 0, shadowVertexes.length);

				shadowTriangle(p2.x, p2.y, s2.x, s2.y, u2.x, u2.y, penumbraColor, shadowVertexes);
				batch.draw(pixel, shadowVertexes, 0, shadowVertexes.length);

				shadowTriangle(u1.x, u1.y, s1.x, s1.y, s2.x, s2.y, umbraColor, shadowVertexes);
				batch.draw(pixel, shadowVertexes, 0, shadowVertexes.length);
				shadowTriangle(u2.x, u2.y, s2.x, s2.y, u1.x, u1.y, umbraColor, shadowVertexes);
				batch.draw(pixel, shadowVertexes, 0, shadowVertexes.length);
			}
		}

		batch.end();
		lightBuffer.end();

		// Blend the light buffer onto the scene
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		batch.begin();
		batch.setShader(null);
		batch.draw(shadowTexture, 0, 0, lightBuffer.getWidth(), lightBuffer.getHeight());
		batch.end();
	}

	private void drawGeometry(List<SimpleShadowCastTest.Light> lights, List<Float> segments) {
		// Draw geometry
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(segmentColor);
		for (int i = 0; i < segments.size() - 3; i += 4) {
			shapeRenderer.line(segments.get(i), segments.get(i+1), segments.get(i+2), segments.get(i+3));
		}
		for (SimpleShadowCastTest.Light light : lights) {
			shapeRenderer.circle(light.origin.x, light.origin.y, light.radius);
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

	public Color getSegmentColor() {
		return segmentColor;
	}
}
