package com.dungeon.game.render.stage;

import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.entity.CreatureEntity;

public class HealthbarStage implements Renderer {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;

	public HealthbarStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
	}

	@Override
	public void render() {
		viewportBuffer.render(batch -> {
			Engine.entities.dynamic().filter(e -> e instanceof CreatureEntity).filter(viewPort::isInViewPort).map(e -> (CreatureEntity)e).forEach(e -> e.drawHealthbar(batch, viewPort));
		});
	}

	@Override
	public void dispose() {}

}
