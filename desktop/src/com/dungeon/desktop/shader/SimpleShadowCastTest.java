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
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
	private FrameBuffer lightBuffer;
	private Texture pixel;
	private Texture normalMap;
	private TextureRegion shadowTexture;
	private Vector2 bufferSize = new Vector2();
	private float time, lastLog;
	private List<Float> geometry = new ArrayList<>();
	private Vector2 startVector = new Vector2();
	private Vector2 cursor = new Vector2();
	// Currently dragging lights
	private boolean dragging = false;
	// Currently drawing geometry
	private boolean drawing = false;
	// Render geometry
	private boolean renderGeometry = true;
	private float[] shadowVertexes = new float[20];
	private final float umbraColor = new Color(0, 0, 0, 1).toFloatBits();
	private final float penumbraColor = new Color(0, 0, 0, 0).toFloatBits();
	private Color ambient = Color.BLACK;//new Color(0.1f, 0.2f, 0.3f, 1.0f);
	private Color segmentColor = Color.RED;
	private LinkedList<Light> lights = new LinkedList<>();
	private Light selectedLight;

	private class Light {
		Vector2 origin;
		float radius;
		float range;
		Color color;

		public Light(Vector2 origin, float radius, float range, Color color) {
			this.origin = origin.cpy();
			this.radius = radius;
			this.range = range;
			this.color = color;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Light light = (Light) o;
			return Float.compare(light.radius, radius) == 0 &&
					Float.compare(light.range, range) == 0 &&
					Objects.equals(origin, light.origin) &&
					Objects.equals(color, light.color);
		}

		@Override
		public int hashCode() {
			return Objects.hash(origin, radius, range, color);
		}

		@Override
		public String toString() {
			return "Light{" +
					"origin=" + origin +
					", radius=" + radius +
					", range=" + range +
					", color=" + color +
					'}';
		}
	}

	@Override
	public void create () {
		normalMapShader = Resources.shaders.get("df_vertex.glsl|test/normal_mapping.glsl");
		shadowShader = Resources.shaders.get("df_vertex.glsl|test/penumbra.glsl");
		normalMap = new Texture("core/assets/normal_map.png");
		pixel = new Texture("core/assets/fill.png");
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		shadowTexture = new TextureRegion(lightBuffer.getColorBufferTexture());
		shadowTexture.flip(false, true);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);
		createRectangleGeometry(800f, 1000f, 350f, 550f);
		createCircleGeometry(new Vector2(200f, 700f), 100f, 20);
		createCircleGeometry(new Vector2(1400f, 300f), 50f, 20);
		createCircleGeometry(new Vector2(1300f, 800f), 50f, 3);
//		// Default light
		lights.add(new Light(new Vector2(100, 100), 10, 1200, new Color(1.0f, 0.5f, 0.0f, 1.0f)));
//		selectedLight = new Light(new Vector2(100, 100), 10f, 1200f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f));
//		lights.add(selectedLight);
	}

	@Override
	public void render() {

		// Clear main buffer
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		int oldSrcFunc = batch.getBlendSrcFunc();
		int oldDstFunc = batch.getBlendDstFunc();

		for (Light light : lights) {
			drawLight(light);
		}
		batch.setBlendFunction(oldSrcFunc, oldDstFunc);

		if (renderGeometry) {
			drawGeometry();
		}

		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
			System.out.println("Lights: " + lights.size());
			System.out.println("Selected Light: " + selectedLight);
		}
	}

	private void drawLight(Light light) {
		lightBuffer.begin();

		// Clear light buffer
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		// Draw light with normal mapping
		normalMapShader.begin();
		normalMapShader.setUniformf("u_lightRange", light.range * (1 + 0.1f * MathUtils.sin(time * 30)));
		normalMapShader.setUniformf("u_lightOrigin", light.origin.x, light.origin.y, 50f);
		normalMapShader.setUniformf("u_lightColor", light.color);
		normalMapShader.setUniformf("u_lightHardness", 1.0f);
		normalMapShader.setUniformf("u_ambientColor", ambient);
		normalMapShader.end();
		batch.setShader(normalMapShader);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
//		batch.setColor(Color.argb8888(0.1f, 0.3f, 0.5f, 1.0f));
		batch.draw(normalMap, 0, 0, bufferSize.x, bufferSize.y);
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
		for (int i = 0; i < geometry.size() - 3; i += 4) {
			s1.set(geometry.get(i), geometry.get(i+1));
			s2.set(geometry.get(i+2), geometry.get(i+3));

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
		batch.draw(shadowTexture, 0, 0, bufferSize.x, bufferSize.y);
		batch.end();
	}

	private void drawGeometry() {
		// Draw geometry
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(segmentColor);
		for (int i = 0; i < geometry.size() - 3; i += 4) {
			shapeRenderer.line(geometry.get(i), geometry.get(i+1), geometry.get(i+2), geometry.get(i+3));
		}
		if (drawing) {
			shapeRenderer.line(startVector.x, startVector.y, cursor.x, cursor.y);
		}
		for (Light light : lights) {
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

	@Override
	public void resize(int width, int height) {
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
			selectedLight = new Light(cursor, 10f, 1000f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f));
			lights.add(selectedLight);
		} else if (keycode == Input.Keys.MINUS) {
			lights.remove(selectedLight);
		} else if (keycode == Input.Keys.F1) {
			renderGeometry = !renderGeometry;
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
		if (button == 0) {
			// Select first light for which cursor is within radius
			selectedLight = null;
			for(Light light : lights) {
				System.err.println("Cursor: " + cursor + ", light.origin: " + light.origin + ", light.radius: " + light.radius + ", distance: " + cursor.dst(light.origin));
				if (cursor.dst(light.origin) < light.radius) {
					selectedLight = light;
					dragging = true;
					break;
				}
			}
		} else  if (button == 1) {
			startVector.set(screenX, bufferSize.y - screenY);
			drawing = true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 0) {
			dragging = false;
		} else if (button == 1) {
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
		cursor.set(screenX, bufferSize.y - screenY);
		if (selectedLight != null && dragging) {
			selectedLight.origin.set(screenX, bufferSize.y - screenY);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		cursor.set(screenX, bufferSize.y - screenY);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		if (selectedLight != null) {
			if (amount > 0) {
				selectedLight.range *= 1.1;
			} else {
				selectedLight.range /= 1.1;
			}
		}
		return true;
	}

	private void createRectangleGeometry(float left, float right, float bottom, float top) {
		geometry.add(left);
		geometry.add(bottom);
		geometry.add(right);
		geometry.add(bottom);

		geometry.add(right);
		geometry.add(bottom);
		geometry.add(right);
		geometry.add(top);

		geometry.add(right);
		geometry.add(top);
		geometry.add(left);
		geometry.add(top);

		geometry.add(left);
		geometry.add(top);
		geometry.add(left);
		geometry.add(bottom);
	}

	private void createCircleGeometry(Vector2 origin, float radius, int segments) {
		Vector2 step = new Vector2(0, radius);
		Vector2 vertex = origin.cpy().add(step);
		for (int i = 0; i < segments; ++i) {
			geometry.add(vertex.x);
			geometry.add(vertex.y);
			step.rotate(360f / segments);
			vertex.set(origin).add(step);
			geometry.add(vertex.x);
			geometry.add(vertex.y);
		}
	}
}