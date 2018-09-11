package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ViewPortRenderer implements Disposable {

	private SpriteBatch batch;

	private final ViewPortBuffer viewportBuffer;

	private int renderCalls = 0;
	private float frameTime;

	private final MapStage mapFragment;
	private final ShadowsStage shadowsFragment;
	private final EntitiesStage entitiesFragment;
	private final LightStage lightFragment;
	private final HealthbarStage healthbarFragment;
	private final CollisionStage collisionFragment;
	private final NoiseStage noiseFragment;
	private final MotionBlurStage motionBlurFragment;
	private final OverlayTextStage overlayTextFragment;
	private final PlayerArrowsStage playerArrowsFragment;
	private final HudStage hudFragment;
	private final ScaleStage scaleFragment;
	private final ConsoleStage consoleFragment;
	private final TitleStage titleStage;
	private final List<RenderStage> pipeline = new ArrayList<>();

	public ViewPortRenderer(ViewPort viewPort, Player player) {
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.batch = new SpriteBatch();

		mapFragment = new MapStage(viewPort, viewportBuffer);
		shadowsFragment = new ShadowsStage(viewPort, viewportBuffer);
		entitiesFragment = new EntitiesStage(viewPort, viewportBuffer);
		lightFragment = new LightStage(viewPort, viewportBuffer);
		healthbarFragment = new HealthbarStage(viewPort, viewportBuffer);
		collisionFragment = new CollisionStage(viewPort, viewportBuffer);
		noiseFragment = new NoiseStage(viewPort, viewportBuffer);
		motionBlurFragment = new MotionBlurStage(viewPort, viewportBuffer);
		scaleFragment = new ScaleStage(viewportBuffer, batch);
		consoleFragment = new ConsoleStage(viewPort, batch, player.getConsole());
		overlayTextFragment = new OverlayTextStage(viewPort, viewportBuffer);
		playerArrowsFragment = new PlayerArrowsStage(viewPort, viewportBuffer);
		hudFragment = new HudStage(viewPort, viewportBuffer, player);
		titleStage = new TitleStage(viewPort, viewportBuffer);

		pipeline.add(mapFragment);
		pipeline.add(shadowsFragment);
		pipeline.add(entitiesFragment);
		pipeline.add(lightFragment);
		pipeline.add(healthbarFragment);
		pipeline.add(collisionFragment);
		pipeline.add(noiseFragment);
		pipeline.add(motionBlurFragment);
		pipeline.add(overlayTextFragment);
		pipeline.add(playerArrowsFragment);
		pipeline.add(hudFragment);
		pipeline.add(titleStage);
		pipeline.add(scaleFragment);
		pipeline.add(consoleFragment);

		// Disable some default disabled
		healthbarFragment.toggle();
		collisionFragment.toggle();
	}

	public void initialize() {
		resetBuffers();
	}

	private void resetBuffers() {
		viewportBuffer.reset();
	}

	public void render () {
		int currentRenderCalls = 0;
		long start = System.nanoTime();

		pipeline.forEach(RenderStage::render);

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

	public void displayTitle(String title) {
		titleStage.display(title);
	}

	public void displayTitle(String title, String subtitle) {
		titleStage.display(title, subtitle);
	}

	@Override
	public void dispose() {
		batch.dispose();
		viewportBuffer.dispose();
		pipeline.forEach(Disposable::dispose);
	}

}
