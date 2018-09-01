package com.dungeon.game.render.stage;

import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.Engine;
import com.dungeon.game.Game;

public class MotionBlurStage implements RenderStage {

	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer blurBuffer;
	private final float opacity;
	private final float duration;
	private float startTime;
	private boolean enabled = true;

	public MotionBlurStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewportBuffer = viewportBuffer;
		this.blurBuffer = new ViewPortBuffer(viewPort);
		this.blurBuffer.reset();
		this.opacity = Game.getConfiguration().getDouble("rendering.blurOpacity", 0.2d).floatValue();
		this.duration = Game.getConfiguration().getDouble("rendering.blurDuration", 4d).floatValue();
	}

	@Override
	public void render() {
		if (enabled) {
			blurBuffer.render((batch) -> {
				// Blend current viewport render in the blur buffer
				float alpha = (Engine.time() - startTime) / duration * (1 - opacity) + opacity;
				batch.setColor(1, 1, 1, alpha);
				viewportBuffer.draw(batch);
				batch.setColor(1, 1, 1, 1);
			});
			// Draw blurred scene back into the viewport buffer
			viewportBuffer.render(blurBuffer::draw);
			if (Engine.time() >= startTime + duration) {
				enabled = false;
			}
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {
		blurBuffer.dispose();
	}

	public void begin() {
		enabled = true;
		startTime = Engine.time();
		blurBuffer.render((batch) -> {
			// Refresh the blur buffer with the latest snapshot
			batch.setColor(1, 1, 1, 1);
			viewportBuffer.draw(batch);
		});
	}

}
