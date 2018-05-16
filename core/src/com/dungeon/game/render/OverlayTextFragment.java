package com.dungeon.game.render;

import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;

import java.util.Comparator;

public class OverlayTextFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Comparator<? super OverlayText> comp = (e1, e2) ->
			e1.getOrigin().y > e2.getOrigin().y ? -1 :
			e1.getOrigin().y < e2.getOrigin().y ? 1 :
			e1.getOrigin().x < e2.getOrigin().x ? -1 :
			e1.getOrigin().x > e2.getOrigin().x ? 1 : 0;
	private boolean enabled = true;

	public OverlayTextFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				// Iterate texts in render order and draw them
				GameState.getOverlayTexts().stream()/*.filter(viewPort::isInViewPort)*/.sorted(comp).forEach(t -> t.draw(batch, viewPort));
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {}

}
