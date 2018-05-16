package com.dungeon.game.render;

import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public class MotionBlurFragment implements RenderFragment {

	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer blurBuffer;
	private final float opacity;
	private boolean enabled = true;

	public MotionBlurFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewportBuffer = viewportBuffer;
		this.blurBuffer = new ViewPortBuffer(viewPort);
		this.blurBuffer.reset();
		this.opacity = GameState.getConfiguration().getDouble("rendering.blurOpacity", 0.2d).floatValue();
	}

	@Override
	public void render() {
		if (enabled) {
			blurBuffer.render((batch) -> {
				// Blend current viewport render in the blur buffer
				batch.setColor(1, 1, 1, opacity);
				viewportBuffer.draw(batch);
				batch.setColor(1, 1, 1, 1);
			});
			// Draw blurred scene back into the viewport buffer
			viewportBuffer.render(blurBuffer::draw);
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

}
