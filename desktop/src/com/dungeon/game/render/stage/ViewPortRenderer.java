package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.render.RenderNode;
import com.dungeon.engine.render.RenderPipeline;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.developer.DevTools;
import com.dungeon.game.player.Player;

import java.util.List;

public class ViewPortRenderer implements Disposable {

	private SpriteBatch batch;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;

	private final SceneStage scene;
	private final HealthbarStage healthbar;
	private final CollisionStage collision;
	private final NoiseStage noise;
	private final MotionBlurStage motionBlur;
	private final OverlayTextStage overlayText;
	private final PlayerArrowsStage playerArrows;
	private final HudStage hud;
	private final MiniMapStage miniMap;
	private final TransitionStage transition;
	private final ScaleStage scale;
	private final ConsoleStage console;
	private final TitleStage title;
	private final RenderPipeline pipeline = new RenderPipeline();

	public ViewPortRenderer(ViewPort viewPort, List<Player> players) {
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.viewPort = viewPort;
		this.batch = new SpriteBatch();

		scene = new SceneStage(viewPort, viewportBuffer);
		healthbar = new HealthbarStage(viewPort, viewportBuffer);
		collision = new CollisionStage(viewPort, viewportBuffer);
		noise = new NoiseStage(viewPort, viewportBuffer);
		motionBlur = new MotionBlurStage(viewPort, viewportBuffer);
		transition = new TransitionStage(viewPort, viewportBuffer);
		scale = new ScaleStage(viewportBuffer, batch);
		console = new ConsoleStage(viewPort, batch, players.get(0).getConsole());
		overlayText = new OverlayTextStage(viewPort, viewportBuffer);
		playerArrows = new PlayerArrowsStage(viewPort, viewportBuffer);
		hud = new HudStage(viewPort, viewportBuffer, batch, players);
		miniMap = new MiniMapStage(viewPort, viewportBuffer, batch);
		title = new TitleStage(viewPort, viewportBuffer);

		pipeline.addNode(scene, "scene");
		pipeline.addNode(healthbar, "healthbar");
		pipeline.addNode(collision, "collision");
		pipeline.addNode(noise, "noise");
		pipeline.addNode(motionBlur, "motion_blur");
		pipeline.addNode(overlayText, "overlay_text");
		pipeline.addNode(playerArrows, "player_arrow");
		pipeline.addNode(title, "title");
		pipeline.addNode(transition, "transition");
		pipeline.addNode(scale, "scale");
		pipeline.addNode(hud, "hud");
		pipeline.addNode(miniMap, "minimap");
		pipeline.addNode(console, "console");

		// Disable some default disabled
		pipeline.findNode("healthbar").ifPresent(RenderNode::toggle);
		pipeline.findNode("collision").ifPresent(RenderNode::toggle);

		// Publish all profilers in the DevTools static class
		pipeline.streamAll().forEach(node -> DevTools.namedProfilers.put(node.getLabel(), node.getProfiler()));
		Game.devTools.refreshProfilerWidgets();
	}

	public void initialize() {
		resetBuffers();
	}

	private void resetBuffers() {
		viewportBuffer.reset();
	}

	public void render () {
		pipeline.render();
	}

	public void toggleTiles() {
		scene.toggleDrawTiles();
	}

	public void toggleEntities() {
		scene.toggleDrawEntities();
	}

	public void toggleLighting() {
		scene.toggleDrawLights();
	}

	public void toggleNormalMapOnly() {
		scene.toggleNormalMapOnly();
	}

	public void toggleShadows() {
		scene.toggleDrawShadows();
	}

	public void toggleHealthbars() {
		pipeline.findNode("healthbar").ifPresent(RenderNode::toggle);
	}

	public void toggleBoundingBox() {
		pipeline.findNode("collision").ifPresent(RenderNode::toggle);
	}

	public void toggleNoise() {
		pipeline.findNode("noise").ifPresent(RenderNode::toggle);
	}

	public void toggleConsole() {
		pipeline.findNode("console").ifPresent(RenderNode::toggle);
	}

	public void displayTitle(String title) {
		this.title.display(title);
	}

	public void displayTitle(String title, String subtitle) {
		this.title.display(title, subtitle);
	}

	public HudStage getHud() {
		return hud;
	}

	public void toggleProfiler() {
		pipeline.toggleProfile();
	}

	public void openTransition(float duration, Runnable endAction) {
		transition.open(duration, endAction);
	}

	public void closeTransition(float duration, Runnable endAction) {
		transition.close(duration, endAction);
	}

	@Override
	public void dispose() {
		batch.dispose();
		viewportBuffer.dispose();
		pipeline.streamAll().forEach(RenderNode::dispose);
	}

}
