package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

public class ScaleStage implements Renderer {

	private final ViewPortBuffer viewportBuffer;
	private final SpriteBatch batch;

	public ScaleStage(ViewPortBuffer viewportBuffer, SpriteBatch batch) {
		this.viewportBuffer = viewportBuffer;
		this.batch = batch;
	}

	@Override
	public void render() {
		batch.begin();
		viewportBuffer.drawScaled(batch);
		batch.end();
	}

	@Override
	public void dispose() {}

}
