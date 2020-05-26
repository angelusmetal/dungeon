package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.resource.Resources;

public class CollisionStage implements Renderer {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Color collision = new Color(1f, 0.2f, 0.2f, 0.3f);
	private final Color origin = new Color(0.2f, 1f, 0.2f, 0.3f);
	private final TextureRegion fill;

	public CollisionStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.fill = new TextureRegion(Resources.textures.get("fill.png"));
	}

	@Override
	public void render() {
		viewportBuffer.render(batch -> {
			batch.setColor(collision);
			Engine.entities.inViewPort(viewPort).filter(viewPort::isInViewPort).forEach(e ->
					batch.draw(fill, e.getBody().getBottomLeft().x, e.getBody().getBottomLeft().y, e.getBody().getBoundingBox().x, e.getBody().getBoundingBox().y)
			);
			batch.setColor(origin);
			Engine.entities.inViewPort(viewPort).filter(viewPort::isInViewPort).forEach(e ->
					batch.draw(fill, e.getOrigin().x, e.getOrigin().y, 1, 1)
			);
			batch.setColor(Color.WHITE);
		});
	}

	@Override
	public void dispose() {}

}
