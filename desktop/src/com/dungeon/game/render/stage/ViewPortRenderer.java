package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

	private final MapStage mapStage;
	private final ShadowsStage shadowsStage;
	private final EntitiesStage entitiesStage;
	private final LightStage lightStage;
	private final HealthbarStage healthbarStage;
	private final CollisionStage collisionStage;
	private final NoiseStage noiseStage;
	private final MotionBlurStage motionBlurStage;
	private final OverlayTextStage overlayTextStage;
	private final PlayerArrowsStage playerArrowsStage;
	private final HudStage hudStage;
	private final MiniMapStage miniMapStage;
	private final ScaleStage scaleStage;
	private final ConsoleStage consoleStage;
	private final TitleStage titleStage;
	private final List<RenderStage> pipeline = new ArrayList<>();

	public ViewPortRenderer(ViewPort viewPort, Player player) {
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.batch = new SpriteBatch();

		mapStage = new MapStage(viewPort, viewportBuffer);
		shadowsStage = new ShadowsStage(viewPort, viewportBuffer);
		entitiesStage = new EntitiesStage(viewPort, viewportBuffer);
		lightStage = new LightStage(viewPort, viewportBuffer);
		healthbarStage = new HealthbarStage(viewPort, viewportBuffer);
		collisionStage = new CollisionStage(viewPort, viewportBuffer);
		noiseStage = new NoiseStage(viewPort, viewportBuffer);
		motionBlurStage = new MotionBlurStage(viewPort, viewportBuffer);
		scaleStage = new ScaleStage(viewportBuffer, batch);
		consoleStage = new ConsoleStage(viewPort, batch, player.getConsole());
		overlayTextStage = new OverlayTextStage(viewPort, viewportBuffer);
		playerArrowsStage = new PlayerArrowsStage(viewPort, viewportBuffer);
		hudStage = new HudStage(viewPort, viewportBuffer, player);
		miniMapStage = new MiniMapStage(viewPort, viewportBuffer);
		titleStage = new TitleStage(viewPort, viewportBuffer);

		pipeline.add(mapStage);
		pipeline.add(shadowsStage);
		pipeline.add(entitiesStage);
		pipeline.add(lightStage);
		pipeline.add(healthbarStage);
		pipeline.add(collisionStage);
		pipeline.add(noiseStage);
		pipeline.add(motionBlurStage);
		pipeline.add(overlayTextStage);
		pipeline.add(playerArrowsStage);
		pipeline.add(hudStage);
		pipeline.add(miniMapStage);
		pipeline.add(titleStage);
		pipeline.add(scaleStage);
		pipeline.add(consoleStage);

		// Disable some default disabled
		healthbarStage.toggle();
		collisionStage.toggle();
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
		mapStage.toggle();
		entitiesStage.toggle();
	}

	public void toggleLighting() {
		lightStage.toggle();
	}

	public void toggleHealthbars() {
		healthbarStage.toggle();
	}

	public void toggleBoundingBox() {
		collisionStage.toggle();
	}

	public void toggleNoise() {
		noiseStage.toggle();
	}

	public void beginMotionBlur() {
		motionBlurStage.begin();
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

	public HudStage getHudStage() {
		return hudStage;
	}

	@Override
	public void dispose() {
		batch.dispose();
		viewportBuffer.dispose();
		pipeline.forEach(Disposable::dispose);
	}

}
