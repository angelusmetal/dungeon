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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;

public class NormalMappingTest extends ApplicationAdapter implements InputProcessor {

	/**
	 * Shader test for trying normal mapping.
	 *
	 * The controls are mouse drag to move the light.
	 *
	 * Keys 1 & 2 control the z coordinate (how apart from the wall the light is), while mouse wheel controls the light
	 * range.
	 * @param arg
	 */
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.width = 1800;
		config.height = 900;
		new LwjglApplication(new NormalMappingTest(), config);
	}

	private ShaderProgram shaderProgram;
	private SpriteBatch batch;
	private Color color = new Color(1.0f, 0.5f, 0.0f, 1.0f);
	private Color ambient = new Color(0.1f, 0.2f, 0.3f, 1.0f);
	private Texture normalMap;
	private Vector3 lightOrigin = new Vector3();
	private float lightRange = 1200f;
	private Vector2 bufferSize = new Vector2();
	private float time, lastLog;
	private float[] geometry = new float[1024];
	private int nextGeometry = 16;
	private Vector2 startVector = new Vector2();

	@Override
	public void create () {
		shaderProgram = Resources.shaders.get("df_vertex.glsl|test/normal_mapping.glsl");
		normalMap = new Texture("core/assets/normal_map.png");
		batch = new SpriteBatch();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);
		System.arraycopy(new float[] {
				800, 350, 1000, 350,
				1000, 350, 1000, 550,
				1000, 550, 800, 550,
				800, 550, 800, 350
		}, 0, geometry, 0, 16);

	}

	@Override
	public void render() {
		shaderProgram.begin();
		shaderProgram.setUniformf("u_lightRange", lightRange * (1 + 0.1f * MathUtils.sin(time * 30)));
		shaderProgram.setUniformf("u_lightOrigin", lightOrigin);
		shaderProgram.setUniformf("u_lightColor", color);
		shaderProgram.setUniformf("u_lightHardness", 1.0f);
		shaderProgram.setUniformf("u_ambientColor", ambient);
		shaderProgram.end();
		batch.setShader(shaderProgram);
		batch.begin();
		batch.draw(normalMap, 0, 0, bufferSize.x, bufferSize.y);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.end();
		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
		}
	}

	@Override
	public void resize(int width, int height) {
		lightOrigin.set(Rand.nextFloat(width), Rand.nextFloat(height), 30f);
		bufferSize.set(width, height);
		System.out.println("Resolution: " + bufferSize);
	}
	@Override
	public void dispose() {
		batch.dispose();
		Resources.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.NUM_1) {
			lightOrigin.z = Math.max(lightOrigin.z - 1, 1);
		} else if (keycode == Input.Keys.NUM_2) {
			lightOrigin.z = Math.min(lightOrigin.z + 1, 100);
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
			startVector.set(screenX, screenY);
		}
		System.out.println(button);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 0) {
			geometry[nextGeometry++] = startVector.x;
			geometry[nextGeometry++] = startVector.y;
			geometry[nextGeometry++] = (float) screenX;
			geometry[nextGeometry++] = (float) screenY;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		lightOrigin.x = screenX;
		lightOrigin.y = bufferSize.y - screenY;
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
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