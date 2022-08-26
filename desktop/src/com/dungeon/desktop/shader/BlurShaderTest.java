package com.dungeon.desktop.shader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Util;

public class BlurShaderTest extends ApplicationAdapter implements InputProcessor {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.width = 1800;
		config.height = 900;
		new LwjglApplication(new BlurShaderTest(), config);
	}

	private float time, lastLog;
	private Vector2 cursor = new Vector2();
	private int samples = 3;
	private float blur = 1;
	private Sprite sprite;
	private SpriteBatch batch;
	private ShaderProgram shaderProgram;

	@Override
	public void create () {
		Resources.initAtlas();
		shaderProgram = Resources.shaders.get("df_vertex.glsl|processing/blur.glsl");
		sprite = Resources.loadSprite("tree_green");
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		Vector2 texelSize = new Vector2(1f / sprite.getTexture().getWidth(), 1f / sprite.getTexture().getHeight());
		//Vector2 texelSize = new Vector2(1f / Gdx.graphics.getWidth(), 1f / Gdx.graphics.getHeight());
		shaderProgram.bind();
		shaderProgram.setUniformf("u_texelSize", texelSize);
		shaderProgram.setUniformi("u_samples", samples);
		shaderProgram.setUniformf("u_blur", blur);
		batch.setShader(shaderProgram);
		batch.begin();
		sprite.setPosition(200, 200);
		sprite.setScale(1f);
		sprite.draw(batch);
		batch.end();
		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
		}
	}


	@Override
	public void dispose() {
		Resources.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
//		if (keycode == Input.Keys.NUM_1) {
//			selectedLight = new Light2(viewPort.screenToWorld(cursor), 10f, 1000f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f), true);
//			lights.add(selectedLight);
//		} else if (keycode == Input.Keys.NUM_2) {
//			lights.remove(selectedLight);
//		} else if (keycode == Input.Keys.NUM_3) {
//			selectedLight = new Light2(viewPort.screenToWorld(cursor), 10f, 1000f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f), false);
//			lights.add(selectedLight);
		if (keycode == Input.Keys.LEFT) {
			samples = (int) Util.clamp(samples - 1, 1, 10);
			System.out.println("samples: " + samples);
		} else if (keycode == Input.Keys.RIGHT) {
			samples = (int) Util.clamp(samples + 1, 1, 10);
			System.out.println("samples: " + samples);
		} else if (keycode == Input.Keys.UP) {
			blur = Util.clamp(blur + 0.1f, 0, 10);
			System.out.println("blur: " + blur);
		} else if (keycode == Input.Keys.DOWN) {
			blur = Util.clamp(blur - 0.1f, 0, 10);
			System.out.println("blur: " + blur);
		}
//		} else if (keycode == Input.Keys.PLUS) {
//			viewPort.setScale(viewPort.getScale() * 1.1f);
//		} else if (keycode == Input.Keys.MINUS) {
//			viewPort.setScale(viewPort.getScale() / 1.1f);
//		} else if (keycode == Input.Keys.F1) {
//			renderer.setRenderGeometry(!renderer.isRenderGeometry());
//		} else if (keycode == Input.Keys.ENTER) {
//			useNormalMapping = !useNormalMapping;
//			renderer.setUseNormalMapping(useNormalMapping);
//		}
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
//			for(Light2 light : lights) {
//				System.err.println("Cursor: " + cursor + ", light.origin: " + light.getOrigin() + ", light.radius: " + light.getRadius() + ", distance: " + cursor.dst(light.getOrigin()));
//				if (viewPort.screenToWorld(cursor).dst(light.getOrigin()) < light.getRadius()) {
//					selectedLight = light;
//					dragging = true;
//					break;
//				}
//			}
//		} else if (button == 1) {
//			segmentStart.set(viewPort.screenToWorld(cursor));
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
//			geometry.add(viewPort.screenToWorld(cursor).x);
//			geometry.add(viewPort.screenToWorld(cursor).y);
//			drawing = false;
//		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		cursor.set(screenX, Gdx.graphics.getHeight() - screenY);
//		if (selectedLight != null && dragging) {
//			selectedLight.getOrigin().set(viewPort.screenToWorld(cursor));
//		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		cursor.set(screenX, Gdx.graphics.getHeight() - screenY);
		return true;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		if (amountY > 0) {
			blur = Util.clamp(blur + 0.1f, 0, 10);
			System.out.println("blur: " + blur);
		} else {
			blur = Util.clamp(blur - 0.1f, 0, 10);
			System.out.println("blur: " + blur);
		}
//		if (selectedLight != null) {
//			if (amount > 0) {
//				selectedLight.setRange(selectedLight.getRange() * 1.1f);
//			} else {
//				selectedLight.setRange(selectedLight.getRange() / 1.1f);
//			}
//		}
		return true;
	}

}