package com.dungeon.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.List;

public class ViewPortRenderer implements Disposable {

	private SpriteBatch batch;

	private final ViewPortBuffer viewportBuffer;

	private int renderCalls = 0;
	private float frameTime;

	private final MapFragment mapFragment;
	private final EntitiesFragment entitiesFragment;
	private final LightFragment lightFragment;
	private final HealthbarFragment healthbarFragment;
	private final CollisionFragment collisionFragment;
	private final NoiseFragment noiseFragment;
	private final MotionBlurFragment motionBlurFragment;
	private final OverlayTextFragment overlayTextFragment;
	private final ScaleFragment scaleFragment;
	private final ConsoleFragment consoleFragment;
	private final List<RenderFragment> pipeline = new ArrayList<>();

	public ViewPortRenderer(ViewPort viewPort) {
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.batch = new SpriteBatch();

		mapFragment = new MapFragment(viewPort, viewportBuffer);
		entitiesFragment = new EntitiesFragment(viewPort, viewportBuffer);
		lightFragment = new LightFragment(viewPort, viewportBuffer);
		healthbarFragment = new HealthbarFragment(viewPort, viewportBuffer);
		collisionFragment = new CollisionFragment(viewPort, viewportBuffer);
		noiseFragment = new NoiseFragment(viewPort, viewportBuffer);
		motionBlurFragment = new MotionBlurFragment(viewPort, viewportBuffer);
		scaleFragment = new ScaleFragment(viewPort, viewportBuffer, batch);
		consoleFragment = new ConsoleFragment(viewPort, batch);
		overlayTextFragment = new OverlayTextFragment(viewPort, viewportBuffer);

		pipeline.add(mapFragment);
		pipeline.add(entitiesFragment);
		pipeline.add(lightFragment);
		pipeline.add(healthbarFragment);
		pipeline.add(collisionFragment);
		pipeline.add(noiseFragment);
		pipeline.add(motionBlurFragment);
		pipeline.add(overlayTextFragment);
		pipeline.add(scaleFragment);
		pipeline.add(consoleFragment);

		// Disable some default disabled
		healthbarFragment.toggle();
		collisionFragment.toggle();
	}

	public void initialize() {
		resetBuffers();
		randomizeBaseLight();
	}

	private void resetBuffers() {
		viewportBuffer.reset();
	}

	public void randomizeBaseLight() {
		lightFragment.randomizeBaseLight();
	}

	public void render () {
		int currentRenderCalls = 0;
		long start = System.nanoTime();

		pipeline.forEach(RenderFragment::render);

//		currentRenderCalls += lightingBuffer.getLastRenderCalls();
//		lightingBuffer.resetLastRenderCalls();
		currentRenderCalls += viewportBuffer.getLastRenderCalls();
		viewportBuffer.resetLastRenderCalls();
		renderCalls = currentRenderCalls;
		frameTime = (System.nanoTime() - start) / 1_000_000f;

	}

	public void toggleScene() {
		mapFragment.toggle();
		entitiesFragment.toggle();
	}

	public void toggleLighting() {
		lightFragment.toggle();
	}

	public void toggleHealthbars() {
		healthbarFragment.toggle();
	}

	public void toggleBoundingBox() {
		collisionFragment.toggle();
	}

	public void toggleNoise() {
		noiseFragment.toggle();
	}

	public void beginMotionBlur() {
		motionBlurFragment.begin();
	}

	public int getRenderCalls() {
		return renderCalls;
	}

	public float getFrameTime() {
		return frameTime;
	}

	@Override
	public void dispose() {
		batch.dispose();
		viewportBuffer.dispose();
		pipeline.forEach(Disposable::dispose);
	}

}
