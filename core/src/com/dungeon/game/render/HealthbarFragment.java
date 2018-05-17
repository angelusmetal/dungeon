package com.dungeon.game.render;

import com.dungeon.engine.entity.Character;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public class HealthbarFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private boolean enabled = true;

	public HealthbarFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				GameState.getEntities().stream().filter(e -> e instanceof Character).filter(viewPort::isInViewPort).map(e -> (Character)e).forEach(e -> e.drawHealthbar(batch, viewPort));
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