package com.dungeon.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

import java.util.Comparator;

public class MapStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Comparator<? super Entity> comp = (e1, e2) ->
			e1.getZIndex() > e2.getZIndex() ? 1 :
			e1.getZIndex() < e2.getZIndex() ? -1 :
			e1.getOrigin().y > e2.getOrigin().y ? -1 :
			e1.getOrigin().y < e2.getOrigin().y ? 1 :
			e1.getOrigin().x < e2.getOrigin().x ? -1 :
			e1.getOrigin().x > e2.getOrigin().x ? 1 : 0;
	private boolean enabled = true;

	public MapStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
	}

	@Override
	public void render() {
		if (enabled) {
			// Draw map
			viewportBuffer.render(this::drawMap);
		} else {
			viewportBuffer.render((batch) -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	private void drawMap(SpriteBatch batch) {
		// Only render the visible portion of the map
		int tSize = GameState.getTileset().tile_size;
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
