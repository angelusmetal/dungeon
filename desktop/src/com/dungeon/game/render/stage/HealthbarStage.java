package com.dungeon.game.render.stage;

import com.dungeon.engine.Engine;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;

public class HealthbarStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private boolean enabled = true;

	public HealthbarStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				Engine.entities.all().filter(e -> e instanceof CreatureEntity).filter(viewPort::isInViewPort).map(e -> (CreatureEntity)e).forEach(e -> e.drawHealthbar(batch, viewPort));
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