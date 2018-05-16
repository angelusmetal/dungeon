package com.dungeon.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public class CollisionFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ColorContext colorContext = new ColorContext(new Color(1f, 0.2f, 0.2f, 0.3f));
	private final TextureRegion fill;
	private boolean enabled = true;

	public CollisionFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.fill = new TextureRegion(ResourceManager.instance().getTexture("fill.png"));
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> colorContext.run(batch, () ->
					GameState.getEntities().stream().filter(viewPort::isInViewPort).forEach(e -> viewPort.draw(
							batch,
							fill,
							e.getBody().getBottomLeft().x,
							e.getBody().getBottomLeft().y,
							e.getBody().getBoundingBox().x,
							e.getBody().getBoundingBox().y))));
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	private void drawMap(SpriteBatch batch) {
		// Only render the visible portion of the map
		int tSize = GameState.getLevelTileset().tile_size;
		int minX = Math.max(0, viewPort.cameraX / tSize);
		int maxX = Math.min(GameState.getLevel().map.length - 1, (viewPort.cameraX + viewPort.width) / tSize) + 1;
		int minY = Math.max(0, viewPort.cameraY / tSize - 1);
		int maxY = Math.min(GameState.getLevel().map[0].length - 1, (viewPort.cameraY + viewPort.height) / tSize);
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				TextureRegion textureRegion = GameState.getLevel().map[x][y].animation.getKeyFrame(GameState.time(), true);
				batch.draw(textureRegion, (x * tSize - viewPort.cameraX), (y * tSize - viewPort.cameraY), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
			}
		}
	}

	@Override
	public void dispose() {}

}
