package com.dungeon.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;

public class ShaderDebugger extends ApplicationAdapter {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.vSyncEnabled = false;
//		config.foregroundFPS = 0;
		config.width = 1800;
		config.height = 900;
		new LwjglApplication(new ShaderDebugger(), config);
	}

	ShaderProgram shaderProgram;
	SpriteBatch batch;
	Color color = Color.RED;
	Texture pixel;
	Vector2 speed = new Vector2();
	Vector2 coord = new Vector2(Rand.nextFloat(1f), Rand.nextFloat(1f));
	float ratio;
	float time, lastLog;

	@Override
	public void create () {
//		shaderProgram = Resources.shaders.get("df_vertex.glsl|outline_border_fragment.glsl");
		shaderProgram = Resources.shaders.get("df_vertex.glsl|test_shader.glsl");
		pixel = new Texture("core/assets/fill.png");
		batch = new SpriteBatch();
		speed.x = 2f / Gdx.graphics.getWidth();
		speed.y = 2f / Gdx.graphics.getHeight();
		ratio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
	}

	@Override
	public void render() {
		shaderProgram.begin();
		shaderProgram.setUniformf("u_color", color);
		shaderProgram.setUniformf("u_size", 0.08f);
		shaderProgram.setUniformf("u_coord", coord);
		shaderProgram.setUniformf("u_ratio", ratio);
		shaderProgram.end();
		batch.setShader(shaderProgram);
		batch.begin();
		batch.setColor(Color.argb8888(0.1f, 0.3f, 0.5f, 1f));
		batch.draw(pixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.end();
		coord.add(speed);
		if (coord.x < 0 || coord.x > 1) {
			speed.x *= -1;
		}
		if (coord.y < 0 || coord.y > 1) {
			speed.y *= -1;
		}
		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
		}
	}

	@Override
	public void resize(int width, int height) {
		speed.x = 2f / Gdx.graphics.getWidth();
		speed.y = 2f / Gdx.graphics.getHeight();
		ratio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
	}
	@Override
	public void dispose() {
		batch.dispose();
		Resources.dispose();
	}
}