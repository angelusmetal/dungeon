package com.dungeon.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

public class ScaleStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final SpriteBatch batch;
	private boolean enabled = true;

	public ScaleStage(ViewPort viewPort, ViewPortBuffer viewportBuffer, SpriteBatch batch) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.batch = batch;
	}

	@Override
	public void render() {
		if (enabled) {
			batch.begin();
			viewportBuffer.drawScaled(batch);
//			currentRenderCalls += batch.renderCalls;
			batch.end();
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {}

}
