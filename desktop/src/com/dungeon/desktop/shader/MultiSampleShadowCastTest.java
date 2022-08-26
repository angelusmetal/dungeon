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
import com.dungeon.engine.util.Rand;

public class MultiSampleShadowCastTest extends ApplicationAdapter implements InputProcessor {

	/**
	 * Shader test for casting soft shadows around geometry. This works by passing the geometry to the shader and
	 * verifying intensity (based on distance from current pixel to light source and light hardness) and occlusion by
	 * any geometry segment on a pixel by pixel basis.
	 *
	 * Also, there's a concept of light radius, which defines the radius of the light source itself (not how far the
	 * light reaches, which is the light range). Multiple samples (as defined in u_sampleCount) determine how many times
	 * this process is repeated between within the light radius. A wider radius translates into a wider penumbra, while
	 * a higher profile count translates into softer (but costlier) shadows; cost grows linearly.
	 *
	 * The amount of geometry segments that need to be verified also significantly increase the cost (since it has to be
	 * done per profile, per pixel). So this approach is very expensive.
	 *
	 * The controls are mouse drag (left click) to move the light. Using the right click adds a geometry segment
	 * (segment start and end coordinates are picked from mouse coordinates upon clicking & un-clicking).
	 *
	 * Keys 1 & 2 decrease and increase profile count, while mouse wheel controls the light range.
	 *
	 * @param arg
	 */
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.width = 1800;
		config.height = 900;
		new LwjglApplication(new MultiSampleShadowCastTest(), config);
	}


	private ShaderProgram shaderProgram;
	private SpriteBatch batch;
	private Texture pixel;
	private Vector2 lightOrigin = new Vector2();
	private Color lightColor = Color.RED;
	private float lightRadius = 10f;
	private float lightRange = 1200f;
	private int sampleCount = 16;
	private Vector2 bufferSize = new Vector2();
	private float time, lastLog;
	private float[] geometry = new float[1024];
	private int nextGeometry = 16;
	private Vector2 startVector = new Vector2();

	@Override
	public void create () {
		shaderProgram = Resources.shaders.get("df_vertex.glsl|test/multisample_shadow_cast.glsl");
		pixel = new Texture("core/assets/fill.png");
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
		shaderProgram.bind();
		shaderProgram.setUniformf("u_bufferSize", bufferSize);
		shaderProgram.setUniformf("u_lightHardness", 1f);
		shaderProgram.setUniformf("u_lightColor", lightColor);
		shaderProgram.setUniformf("u_lightRadius", lightRadius);
		shaderProgram.setUniformf("u_lightRange", lightRange);
		shaderProgram.setUniformf("u_lightOrigin", lightOrigin);
		shaderProgram.setUniform4fv("u_segments[0]", geometry, 0, geometry.length);
		shaderProgram.setUniformi("u_segmentCount", geometry.length / 4);
		shaderProgram.setUniformf("u_sampleCount", sampleCount);
		batch.setShader(shaderProgram);
		batch.begin();
		batch.setColor(0.1f, 0.3f, 0.5f, 1f);
		batch.draw(pixel, 0, 0, bufferSize.x, bufferSize.y);
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
		if (keycode == Input.Keys.NUM_1 && sampleCount > 1) {
			sampleCount /= 2;
		} else if (keycode == Input.Keys.NUM_2) {
			sampleCount *= 2;
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
			startVector.set(screenX, screenY);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 1) {
			geometry[nextGeometry++] = startVector.x;
			geometry[nextGeometry++] = startVector.y;
			geometry[nextGeometry++] = (float) screenX;
			geometry[nextGeometry++] = (float) screenY;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		lightOrigin.set(screenX, screenY);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		if (amountY > 0) {
			lightRange *= 1.1;
		} else {
			lightRange /= 1.1;
		}
		return true;
	}
}