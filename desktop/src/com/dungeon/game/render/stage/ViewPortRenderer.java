package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.CyclicSampler;
import com.dungeon.engine.util.StopWatch;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Dungeon;
import com.dungeon.game.developer.DevTools;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;

import java.util.ArrayList;
import java.util.List;

public class ViewPortRenderer implements Disposable {

	private SpriteBatch batch;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;

	private int renderCalls = 0;
	private float frameTime;

	private final SceneStage sceneStage;
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
	private final List<Stage> pipeline = new ArrayList<>();

	private final StopWatch stopWatch = new StopWatch();
	private static class Stage {
		RenderStage stage;
		CyclicSampler sampler;
		Stage(RenderStage stage, CyclicSampler sampler) {
			this.stage = stage;
			this.sampler = sampler;
		}
	}

	public ViewPortRenderer(ViewPort viewPort, List<Player> players) {
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.viewPort = viewPort;
		this.batch = new SpriteBatch();

		sceneStage = new SceneStage(viewPort, viewportBuffer);
		healthbarStage = new HealthbarStage(viewPort, viewportBuffer);
		collisionStage = new CollisionStage(viewPort, viewportBuffer);
		noiseStage = new NoiseStage(viewPort, viewportBuffer);
		motionBlurStage = new MotionBlurStage(viewPort, viewportBuffer);
		scaleStage = new ScaleStage(viewportBuffer, batch);
		consoleStage = new ConsoleStage(viewPort, batch, players.get(0).getConsole());
		overlayTextStage = new OverlayTextStage(viewPort, viewportBuffer);
		playerArrowsStage = new PlayerArrowsStage(viewPort, viewportBuffer);
		hudStage = new HudStage(viewPort, viewportBuffer, batch, players);
		miniMapStage = new MiniMapStage(viewPort, viewportBuffer, batch);
		titleStage = new TitleStage(viewPort, viewportBuffer);

		pipeline.add(new Stage(sceneStage, DevTools.sceneSampler));
		pipeline.add(new Stage(healthbarStage, DevTools.healthbarSampler));
		pipeline.add(new Stage(collisionStage, DevTools.collisionSampler));
		pipeline.add(new Stage(noiseStage, DevTools.noiseSampler));
		pipeline.add(new Stage(motionBlurStage, DevTools.motionBlurSampler));
		pipeline.add(new Stage(overlayTextStage, DevTools.overlayTextSampler));
		pipeline.add(new Stage(playerArrowsStage, DevTools.playerArrowsSampler));
		pipeline.add(new Stage(titleStage, DevTools.titleSampler));
		pipeline.add(new Stage(scaleStage, DevTools.scaleSampler));
		pipeline.add(new Stage(hudStage, DevTools.hudSampler));
		pipeline.add(new Stage(miniMapStage, DevTools.miniMapSampler));
		pipeline.add(new Stage(consoleStage, DevTools.consoleSampler));

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

		stopWatch.start();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width, viewPort.height);
		pipeline.forEach(stage -> {
			stage.stage.render();
			stage.sampler.sample((int) stopWatch.getAndReset());
		});

//		currentRenderCalls += lightingBuffer.getLastRenderCalls();
//		lightingBuffer.resetLastRenderCalls();
		currentRenderCalls += viewportBuffer.getLastRenderCalls();
		viewportBuffer.resetLastRenderCalls();
		renderCalls = currentRenderCalls;
		frameTime = (System.nanoTime() - start) / 1_000_000f;

	}

	public void toggleTiles() {
		sceneStage.toggleDrawTiles();
	}

	public void toggleEntities() {
		sceneStage.toggleDrawEntities();
	}

	public void toggleLighting() {
		sceneStage.toggleDrawLights();
	}

	public void toggleShadows() {
		sceneStage.toggleDrawShadows();
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

	public void toggleConsole() {
		consoleStage.toggle();
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
		pipeline.forEach(element -> element.stage.dispose());
	}

}
