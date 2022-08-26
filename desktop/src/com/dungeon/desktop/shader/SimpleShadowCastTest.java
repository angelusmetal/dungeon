package com.dungeon.desktop.shader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.light.Light2;
import com.dungeon.engine.render.light.LightRenderer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.LinkedList;
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

	// Buffer with normal map information
	private FrameBuffer normalMapBuffer;
	// Patch of texture
	private Texture normalMap;

	// Time
	private float time, lastLog;
	private Vector2 segmentStart = new Vector2();
	private Vector2 cursor = new Vector2();
	// Currently dragging lights
	private boolean dragging = false;
	// Currently drawing geometry
	private boolean drawing = false;
	private Light2 selectedLight;

	private LinkedList<Light2> lights = new LinkedList<>();
	private List<Float> geometry = new ArrayList<>();
	private final LightRenderer renderer = new LightRenderer();
	private boolean useNormalMapping = true;
	private ViewPort viewPort;


	@Override
	public void create () {
		viewPort = new ViewPort(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
		normalMap = new Texture("core/assets/normal_map.png");
		// Normal map buffer
		normalMapBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		renderer.create(viewPort, normalMapBuffer);

		Gdx.input.setInputProcessor(this);
		Segments.rectangle(geometry, 800f, 1000f, 350f, 550f);
		Segments.circle(geometry, new Vector2(200f, 700f), 100f, 20);
		Segments.circle(geometry, new Vector2(1400f, 300f), 50f, 20);
		Segments.circle(geometry, new Vector2(1300f, 800f), 50f, 3);
//		// Default light
		lights.add(new Light2(new Vector2(100, 100), 50f, 10, 1200, new Color(1.0f, 0.5f, 0.0f, 1.0f), true));
//		selectedLight = new Light(new Vector2(100, 100), 10f, 1200f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f));
//		lights.add(selectedLight);

		// Tile-repeat the normal map texture into the normal map buffer
		normalMapBuffer.begin();
		int timesX = (int) Math.ceil(((double) Gdx.graphics.getWidth()) / normalMap.getWidth());
		int timesY = (int) Math.ceil(((double) Gdx.graphics.getHeight()) / normalMap.getHeight());
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		for (int x = 0; x < timesX; ++x) {
			for (int y = 0; y < timesY; ++y) {
				batch.draw(normalMap, x * normalMap.getWidth(), y * normalMap.getHeight(), normalMap.getWidth(), normalMap.getHeight());
			}
		}
		// Additional patch at the bottom left to verify alignment
		batch.draw(normalMap, 20, 20, normalMap.getWidth(), normalMap.getHeight());
		batch.end();
		normalMapBuffer.end();
		batch.dispose();
	}

	@Override
	public void render() {
		List<Float> geometryToRender;
		if (drawing) {
			geometryToRender = new ArrayList<>(geometry);
			geometryToRender.add(segmentStart.x);
			geometryToRender.add(segmentStart.y);
			geometryToRender.add(viewPort.screenToWorld(cursor).x);
			geometryToRender.add(viewPort.screenToWorld(cursor).y);
		} else {
			geometryToRender = geometry;
		}
		renderer.render(lights, geometryToRender);
		renderer.drawToScreen();
		time += Gdx.graphics.getDeltaTime();
		if (time > lastLog + 1.0) {
			lastLog = time;
			System.out.println(Gdx.graphics.getFramesPerSecond());
			System.out.println("Lights: " + lights.size());
			System.out.println("Selected Light: " + selectedLight);
		}
	}


//	@Override
//	public void resize(int width, int height) {
//	}
	@Override
	public void dispose() {
		renderer.dispose();
		normalMapBuffer.dispose();
		Resources.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.NUM_1) {
			selectedLight = new Light2(viewPort.screenToWorld(cursor), 50f, 10f, 1000f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f), true);
			lights.add(selectedLight);
		} else if (keycode == Input.Keys.NUM_2) {
			lights.remove(selectedLight);
		} else if (keycode == Input.Keys.NUM_3) {
			selectedLight = new Light2(viewPort.screenToWorld(cursor), 50f, 10f, 1000f, new Color(Rand.between(0f, 1f), Rand.between(0f, 1f), Rand.between(0f, 1f), 1f), false);
			lights.add(selectedLight);
		} else if (keycode == Input.Keys.LEFT) {
			viewPort.cameraX -= 10;
		} else if (keycode == Input.Keys.RIGHT) {
			viewPort.cameraX += 10;
		} else if (keycode == Input.Keys.UP) {
			viewPort.cameraY += 10;
		} else if (keycode == Input.Keys.DOWN) {
			viewPort.cameraY -= 10;
		} else if (keycode == Input.Keys.PLUS) {
			viewPort.setScale(viewPort.getScale() * 1.1f);
		} else if (keycode == Input.Keys.MINUS) {
			viewPort.setScale(viewPort.getScale() / 1.1f);
		} else if (keycode == Input.Keys.F1) {
			renderer.setRenderGeometry(!renderer.isRenderGeometry());
		} else if (keycode == Input.Keys.ENTER) {
			useNormalMapping = !useNormalMapping;
			renderer.setUseNormalMapping(useNormalMapping);
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
			for(Light2 light : lights) {
				System.err.println("Cursor: " + cursor + ", light.origin: " + light.getOrigin() + ", light.radius: " + light.getRadius() + ", distance: " + cursor.dst(light.getOrigin()));
				if (viewPort.screenToWorld(cursor).dst(light.getOrigin()) < light.getRadius()) {
					selectedLight = light;
					dragging = true;
					break;
				}
			}
		} else if (button == 1) {
			segmentStart.set(viewPort.screenToWorld(cursor));
			drawing = true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == 0) {
			dragging = false;
		} else if (button == 1) {
			geometry.add(segmentStart.x);
			geometry.add(segmentStart.y);
			geometry.add(viewPort.screenToWorld(cursor).x);
			geometry.add(viewPort.screenToWorld(cursor).y);
			drawing = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		cursor.set(screenX, Gdx.graphics.getHeight() - screenY);
		if (selectedLight != null && dragging) {
			selectedLight.getOrigin().set(viewPort.screenToWorld(cursor));
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		cursor.set(screenX, Gdx.graphics.getHeight() - screenY);
		return true;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		if (selectedLight != null) {
			if (amountY > 0) {
				selectedLight.setRange(selectedLight.getRange() * 1.1f);
			} else {
				selectedLight.setRange(selectedLight.getRange() / 1.1f);
			}
		}
		return true;
	}

}