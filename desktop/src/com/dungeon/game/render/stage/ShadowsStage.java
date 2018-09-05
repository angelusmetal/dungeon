package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.resource.Resources;

public class ShadowsStage implements RenderStage {

	private static final float MAX_HEIGHT_ATTENUATION = 100;
	private static final int VERTICAL_OFFSET = -2;
	private static final float SHADOW_INTENSITY = 0.6f;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final TextureRegion texture;
	private boolean enabled = true;
	private final Color color = new Color(1, 1, 1, SHADOW_INTENSITY);

	public ShadowsStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		texture = new TextureRegion(Resources.textures.get("shadow.png"));
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				// Iterate entities in render order and draw them
				Engine.entities.all()
						.filter(Entity::castsShadow)
						.filter(viewPort::isInViewPort)
						.forEach(entity -> {
					color.a = SHADOW_INTENSITY * entity.getColor().a;
					batch.setColor(color);
					float attenuation = 1 - Math.min(entity.getZPos(), MAX_HEIGHT_ATTENUATION) / MAX_HEIGHT_ATTENUATION;
					float width = entity.getBody().getBoundingBox().x * attenuation;
					float height = width / 3 * attenuation;
					viewPort.draw(batch,
							texture,
							entity.getBody().getBottomLeft().x,
							entity.getBody().getBottomLeft().y + VERTICAL_OFFSET,
							width,
							height);
				});
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