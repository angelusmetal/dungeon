package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

public class ScaleStage implements RenderStage {

	private final ViewPortBuffer viewportBuffer;
	private final SpriteBatch batch;
	private boolean enabled = true;

	public ScaleStage(ViewPortBuffer viewportBuffer, SpriteBatch batch) {
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
