package com.dungeon.desktop.shader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.resource.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleShadowCastTest extends ApplicationAdapter implements InputProcessor {

	/**
	 * Shader test for casting soft shadows around geometry and using normal mapping. This works by doing a first pass
	 * with a normal mapping shader, and then a second pass where we draw triangles to cut shadows around geometry.
	 * To achieve soft shadows, the shadows are drawn using a special umbra/penumbra shader. If umbra is desired, the
	 * provided vertex color has alpha = 1 (so the shadow is completely solid). If penumbra is desired, the provided
	 * vertex color has alpha = 0, and the shader fragment correctly smooths the triangle having one side completely
	 * solid and the other one completely transparent (using 3 vertex colors on each vertex does not achieve this).
	 *
	 * 2 solid triangles are drawn from each segment towards infinity (away from the light source), and 2 additional
	 * triangles are drawn on each side by displacing the light origin to the side (the amount of displacement is light
	 * radius); when this is intersected with each end of the segment, a secondary set of triangles is rendered and
	 * the uv coordinates are aligned so the transparent sides point outwards, while the solid sides point towards the
	 * solid shadow triangles drawn previously.
	 *
	 * Also, the blending mode is set to GL_ONE, GL_ONE, so overlapping shadows further obscure; otherwise soft shadows
	 * can restore transparency on previously black pixels.
	 *
	 * This approach is a very good approximation and runs very fast (over 300 times faster than the approach on
	 * {@link MultiSampleShadowCastTest}).
	 *
	 * @param arg
	 */
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.width = 1800;
		config.height = 900;
		new LwjglApplication(new SimpleShadowCastTest(), config);
	}

	private ShaderProgram shadowShader;
	private ShaderProgram normalMapShader;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private FrameBuffer shadowBuffer;
	private Texture pixel;
	private Texture normalMap;
	private TextureRegion shadowTexture;
	private Vector2 lightOrigin = new Vector2();
	private float lightRadius = 10f;
	private float lightRange = 1200f;
	private Vector2 bufferSize = new Vector2();
	private float time, lastLog;
	private List<Float> geometry = new ArrayList<>();
	private Vector2 startVector = new Vector2();
	private boolean drawing = false;
	private float[] shadowVertexes = new float[20];
	private final float umbraColor = new Color(0, 0, 0, 1).toFloatBits();
	private final float penumbraColor = new Color(0, 0, 0, 0).toFloatBits();
	private Color lightColor = new Color(1.0f, 0.5f, 0.0f, 1.0f);
	private Color ambient = Color.BLACK;//new Color(0.1f, 0.2f, 0.3f, 1.0f);
	private Color segmentColor = Color.BLACK;

	@Override
	public void create () {
		normalMapShader = Resources.shaders.get("df_vertex.glsl|test/normal_mapping.glsl");
		shadowShader = Resources.shaders.get("df_vertex.glsl|test/penumbra.glsl");
		normalMap = new Texture("core/assets/normal_map.png");
		pixel = new Texture("core/assets/fill.png");
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		shadowBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		shadowTexture = new TextureRegion(shadowBuffer.getColorBufferTexture());
		shadowTexture.flip(false, true);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);
		geometry.addAll(Arrays.asList(
				800f, 350f, 1000f, 350f,
				1000f, 350f, 1000f, 550f,
				1000f, 550f, 800f, 550f,
				800f, 550f, 800f, 350f
		));

	}

	@Override
	public void render() {
		// Draw light with normal mapping
		normalMapShader.begin();
		normalMapShader.setUniformf("u_lightRange", lightRange * (1 + 0.1f * MathUtils.sin(time * 30)));
		normalMapShader.setUniformf("u_lightOrigin", lightOrigin.x, lightOrigin.y, 50f);
		normalMapShader.setUniformf("u_lightColor", lightColor);
		normalMapShader.setUniformf("u_lightHardness", 1.0f);
		normalMapShader.setUniformf("u_ambientColor", ambient);
		normalMapShader.end();
		batch.setShader(normalMapShader);
		batch.begin();
//		batch.setColor(Color.argb8888(0.1f, 0.3f, 0.5f, 1.0f));
		batch.draw(normalMap, 0, 0, bufferSize.x, bufferSize.y);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.end();

		shadowBuffer.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw shadows
		int oldSrcFunc = batch.getBlendSrcFunc();
		int oldDstFunc = batch.getBlendDstFunc();
		batch.begin();
		batch.setShader(shadowShader);
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		// Draw each shadow
		Vector2 normal = new Vector2();
		Vector2 s1 = new Vector2();
		Vector2 s2 = new Vector2();
		Vector2 p1 = new Vector2();
		Vector2 p2 = new Vector2();
		Vector2 u1 = new Vector2();
		Vector2 u2 = new Vector2();
		for (int i = 0; i < geometry.size() - 3; i += 4) {
			s1.set(geometry.get(i), geometry.get(i+1));
			s2.set(geometry.get(i+2), geometry.get(i+3));

			u1.set(s1).sub(lightOrigin).scl(1000f).add(lightOrigin);
			u2.set(s2).sub(lightOrigin).scl(1000f).add(lightOrigin);
			// Shadows are only drawn one way - this reduces a lot of artifacts
			if (orientation(s1, s2, lightOrigin) > 0) {
				normal.set(s1).sub(lightOrigin).nor().rotate90(1).scl(lightRadius);
				p1.set(s1).sub(lightOrigin).add(normal).scl(1000f).add(lightOrigin);
				normal.set(s2).sub(lightOrigin).nor().rotate90(1).scl(lightRadius);
				p2.set(s2).sub(lightOrigin).sub(normal).scl(1000f).add(lightOrigin);
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
		shadowBuffer.end();
		batch.begin();
		batch.setBlendFunction(oldSrcFunc, oldDstFunc);
		batch.setShader(null);
		batch.draw(shadowTexture, 0, 0, bufferSize.x, bufferSize.y);
		batch.end();

		// Draw geometry
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(segmentColor);
		for (int i = 0; i < geometry.size() - 3; i += 4) {
			shapeRenderer.line(geometry.get(i), geometry.get(i+1), geometry.get(i+2), geometry.get(i+3));
		}
		if (drawing) {
			shapeRenderer.line(startVector.x, startVector.y, lightOrigin.x, lightOrigin.y);
		}
		shapeRenderer.circle(lightOrigin.x, lightOrigin.y, lightRadius);
		shapeRenderer.end();

		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
		}
	}

	// To find orientation of ordered triplet (p, q, r).
	// The function returns following values
	// 0 --> p, q and r are colinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	float orientation(Vector2 p, Vector2 q, Vector2 r) {
		// See https://www.geeksforgeeks.org/orientation-3-ordered-points/
		// for details of below formula.
		return  (q.y - p.y) * (r.x - q.x) -
				(q.x - p.x) * (r.y - q.y);

//    if (val == 0) return 0; // colinear
//
//    return (val > 0)? 1: 2; // clock or counterclock wise
	}

	private void shadowTriangle(float x, float y, float x2, float y2, float x3, float y3, float c, float[] vertexes) {
		vertexes[0] = x;
		vertexes[1] = y;
		vertexes[2] = c;
		vertexes[3] = 0f;
		vertexes[4] = 0f;
		vertexes[5] = x2;
		vertexes[6] = y2;
		vertexes[7] = c;
		vertexes[8] = 0f;
		vertexes[9] = 1f;
		vertexes[10] = x3;
		vertexes[11] = y3;
		vertexes[12] = c;
		vertexes[13] = 1f;
		vertexes[14] = 0f;
		vertexes[15] = x;
		vertexes[16] = y;
		vertexes[17] = c;
		vertexes[18] = 1f;
		vertexes[19] = 1f;
	}

	@Override
	public void resize(int width, int height) {
//		lightOrigin.set(Rand.nextFloat(width), Rand.nextFloat(height));
		bufferSize.set(width, height);
		shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, bufferSize.x, bufferSize.y);
		System.out.println("Resolution: " + bufferSize);
	}
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		Resources.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.PLUS) {
//			sampleCount++;
		} else if (keycode == Input.Keys.MINUS) {
//			sampleCount--;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == 1) {
			startVector.set(screenX, bufferSize.y - screenY);
			drawing = true;
		}
		lightOrigin.set(screenX, bufferSize.y - screenY);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 1) {
			geometry.add(startVector.x);
			geometry.add(startVector.y);
			geometry.add((float)screenX);
			geometry.add(bufferSize.y - screenY);
			drawing = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		lightOrigin.set(screenX, bufferSize.y - screenY);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		if (amount > 0) {
			lightRange *= 1.1;
		} else {
			lightRange /= 1.1;
		}
		return true;
	}
}