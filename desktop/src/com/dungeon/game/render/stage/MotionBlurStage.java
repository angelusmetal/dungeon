package com.dungeon.game.render.stage;

import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

public class MotionBlurStage implements Renderer {

	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer blurBuffer;
	private final float opacity;
	private final float duration;
	private float startTime;

	public MotionBlurStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewportBuffer = viewportBuffer;
		this.blurBuffer = new ViewPortBuffer(viewPort);
		this.blurBuffer.reset();
		this.opacity = ConfigUtil.getFloat(Game.getConfiguration(), "rendering.blurOpacity").orElse(0.2f);
		this.duration = ConfigUtil.getFloat(Game.getConfiguration(), "rendering.blurDuration").orElse(4f);
	}

	@Override
	public void render() {
		blurBuffer.render(batch -> {
			// Blend current viewport render in the blur buffer
			float alpha = (Engine.time() - startTime) / duration * (1 - opacity) + opacity;
			batch.setColor(1, 1, 1, alpha);
			viewportBuffer.draw(batch);
			batch.setColor(1, 1, 1, 1);
		});
		// Draw blurred scene back into the viewport buffer
		viewportBuffer.render(blurBuffer::draw);
//		if (Engine.time() >= startTime + duration) {
//			enabled = false;
//		}
	}

	@Override
	public void dispose() {
		blurBuffer.dispose();
	}

	public void begin() {
//		enabled = true;
		startTime = Engine.time();
		blurBuffer.render(batch -> {
			// Refresh the blur buffer with the latest snapshot
			batch.setColor(1, 1, 1, 1);
			viewportBuffer.draw(batch);
		});
	}

}
