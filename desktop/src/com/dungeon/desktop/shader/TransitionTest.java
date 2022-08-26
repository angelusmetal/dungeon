package com.dungeon.desktop.shader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.resource.Resources;

import java.util.ArrayList;
import java.util.List;

public class TransitionTest extends ApplicationAdapter implements InputProcessor {

	/**
	 * Shader test for drawing a transition to black (or any other color) between levels.
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
		new LwjglApplication(new TransitionTest(), config);
	}

//	// Buffer with normal map information
//	private FrameBuffer normalMapBuffer;
//	// Patch of texture
//	private Texture normalMap;

	// Time
	private float time, lastLog;
//	private Vector2 segmentStart = new Vector2();
//	private Vector2 cursor = new Vector2();
//	// Currently dragging lights
//	private boolean dragging = false;
//	// Currently drawing geometry
//	private boolean drawing = false;
//	private Light selectedLight;

//	private LinkedList<Light> lights = new LinkedList<>();
//	private List<Float> geometry = new ArrayList<>();
//	private final LightRenderer renderer = new LightRenderer();

	private Texture pixel;
	private List<ShaderProgram> shaderPrograms = new ArrayList<>();
	private SpriteBatch batch;
	private int selectedShader = 0;

	@Override
	public void create () {
		pixel = new Texture("core/assets/fill.png");
		shaderPrograms.add(Resources.shaders.get("df_vertex.glsl|transition/circle.glsl"));
		shaderPrograms.add(Resources.shaders.get("df_vertex.glsl|transition/bubbles.glsl"));
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.5f, 0.3f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		ShaderProgram shaderProgram = shaderPrograms.get(selectedShader);
		shaderProgram.bind();
		shaderProgram.setUniformf("u_bufferSize", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		float phase = (float) Math.sin(time);//(Math.sin(time) + 1f) / 2f;
		shaderProgram.setUniformf("u_phase", phase);
		batch.setShader(shaderProgram);
		batch.begin();
		batch.setColor(new Color(0f, 0f, 0f, 1f));
		batch.draw(pixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
		}
	}


//	@Override
//	public void resize(int width, int height) {
//	}
	@Override
	public void dispose() {
		batch.dispose();
		Resources.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.NUM_2 && selectedShader < shaderPrograms.size() - 1) {
			selectedShader++;
		} else if (keycode == Input.Keys.NUM_1 && selectedShader > 0) {
			selectedShader--;
//		} else if (keycode == Input.Keys.F1) {
//			renderer.setRenderGeometry(!renderer.isRenderGeometry());
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
//		if (button == 0) {
//			// Select first light for which cursor is within radius
//			selectedLight = null;
//			for(Light light : lights) {
//				System.err.println("Cursor: " + cursor + ", light.origin: " + light.origin + ", light.radius: " + light.radius + ", distance: " + cursor.dst(light.origin));
//				if (cursor.dst(light.origin) < light.radius) {
//					selectedLight = light;
//					dragging = true;
//					break;
//				}
//			}
//		} else  if (button == 1) {
//			segmentStart.set(screenX, Gdx.graphics.getHeight() - screenY);
//			drawing = true;
//		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		if (button == 0) {
//			dragging = false;
//		} else if (button == 1) {
//			geometry.add(segmentStart.x);
//			geometry.add(segmentStart.y);
//			geometry.add((float)screenX);
//			geometry.add((float) Gdx.graphics.getHeight() - screenY);
//			drawing = false;
//		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		cursor.set(screenX, Gdx.graphics.getHeight() - screenY);
//		if (selectedLight != null && dragging) {
//			selectedLight.origin.set(screenX, Gdx.graphics.getHeight() - screenY);
//		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
//		cursor.set(screenX, Gdx.graphics.getHeight() - screenY);
		return true;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
//		if (selectedLight != null) {
//			if (amount > 0) {
//				selectedLight.range *= 1.1;
//			} else {
//				selectedLight.range /= 1.1;
//			}
//		}
		return true;
	}

}