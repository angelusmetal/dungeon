package com.dungeon.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public class CollisionStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Color collision = new Color(1f, 0.2f, 0.2f, 0.3f);
	private final Color origin = new Color(0.2f, 1f, 0.2f, 0.3f);
	private final TextureRegion fill;
	private boolean enabled = true;

	public CollisionStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.fill = new TextureRegion(ResourceManager.getTexture("fill.png"));
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				batch.setColor(collision);
				GameState.getEntities().stream().filter(viewPort::isInViewPort).forEach(e ->
						viewPort.draw(
								batch,
								fill,
								e.getBody().getBottomLeft().x,
								e.getBody().getBottomLeft().y,
								e.getBody().getBoundingBox().x,
								e.getBody().getBoundingBox().y));
				batch.setColor(origin);
				GameState.getEntities().stream().filter(viewPort::isInViewPort).forEach(e ->
					viewPort.draw(
							batch,
							fill,
							e.getOrigin().x,
							e.getOrigin().y,
							1,
							1));
				batch.setColor(Color.WHITE);
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