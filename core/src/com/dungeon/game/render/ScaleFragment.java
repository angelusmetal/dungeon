package com.dungeon.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

public class ScaleFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final SpriteBatch batch;
	private boolean enabled = true;

	public ScaleFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer, SpriteBatch batch) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.batch = batch;
	}

	@Override
	public void render() {
		if (enabled) {
			batch.begin();
//		batch.setColor(new Color(1,1,1,0.2f));
			viewportBuffer.drawScaled(batch);
//			currentRenderCalls += batch.renderCalls;
//		batch.setColor(Color.WHITE);
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
