package com.dungeon.game.viewport;

import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.render.stage.ViewPortRenderer;

/**
 * A View into the game; composed of a viewport, its renderer and a camera that will update the viewport.
 */
public class View {
	private final ViewPort viewPort;
	private final ViewPortRenderer renderer;
	private final ViewPortCamera camera;

	public View(ViewPort viewPort, ViewPortRenderer renderer, ViewPortCamera camera) {
		this.viewPort = viewPort;
		this.renderer = renderer;
		this.camera = camera;
	}

	public void updateCamera() {
		camera.update();
	}

	public void render() {
		renderer.render();
	}

	public void dispose() {
		renderer.dispose();
	}
}
