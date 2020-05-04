package com.dungeon.desktop;

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
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;

public class ShaderDebugger extends ApplicationAdapter implements InputProcessor {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.width = 1800;
		config.height = 900;
		new LwjglApplication(new ShaderDebugger(), config);
	}


	ShaderProgram shaderProgram;
	SpriteBatch batch;
	Color color = new Color(1.0f, 0.5f, 0.0f, 1.0f);
	Color ambient = new Color(0.125f, 0.075f, 0.025f, 1.0f);
	Texture normalMap;
	Vector2 speed = new Vector2();
	Vector2 lightOrigin = new Vector2();
	float lightRadius = 10f;
	private float lightRange = 1200f;
	private int sampleCount = 1;
	Vector2 bufferSize = new Vector2();
	float time, lastLog;
	float[] geometry = new float[1024];
	int nextGeometry = 16;
	Vector2 startVector = new Vector2();

	@Override
	public void create () {
//		shaderProgram = Resources.shaders.get("df_vertex.glsl|outline_border_fragment.glsl");
		shaderProgram = Resources.shaders.get("df_vertex.glsl|test_shader.glsl");
		normalMap = new Texture("core/assets/normal_map.png");
		batch = new SpriteBatch();
		speed.set(0.03f, 0.03f);
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
		shaderProgram.setUniformf("u_lightColor", color);
//		shaderProgram.setUniformf("u_lightRadius", lightRadius);
		shaderProgram.setUniformf("u_lightRange", lightRange * (1 + 0.1f * MathUtils.sin(time * 30)));
		shaderProgram.setUniformf("u_lightOrigin", lightOrigin);
//		shaderProgram.setUniform4fv("u_segments[0]", geometry, 0, geometry.length);
//		shaderProgram.setUniformi("u_segmentCount", geometry.length / 4);
//		shaderProgram.setUniformi("u_sampleCount", sampleCount);
		shaderProgram.end();
		batch.setShader(shaderProgram);
		batch.begin();
//		batch.setColor(Color.argb8888(0.1f, 0.3f, 0.5f, 1.0f));
		batch.draw(normalMap, 0, 0, bufferSize.x, bufferSize.y);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.end();
//		lightOrigin.add(speed);
		if (lightOrigin.x < 0 || lightOrigin.x > bufferSize.x) {
			speed.x *= -1;
		}
		if (lightOrigin.y < 0 || lightOrigin.y > bufferSize.y) {
			speed.y *= -1;
		}
		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
		}
	}

	@Override
	public void resize(int width, int height) {
		lightOrigin.set(Rand.nextFloat(width), Rand.nextFloat(height));
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
		if (keycode == Input.Keys.PLUS) {
			sampleCount = Math.min(sampleCount + 1, 100);
		} else if (keycode == Input.Keys.MINUS) {
			sampleCount = Math.max(sampleCount - 1, 1);
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
		lightOrigin.set(screenX, bufferSize.y - screenY);
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