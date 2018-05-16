package com.dungeon.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.Console;
import com.dungeon.game.state.GameState;

import java.util.Map;
import java.util.function.Supplier;

public class ConsoleFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final SpriteBatch batch;
	private final BitmapFont font;
	private boolean enabled = true;

	public ConsoleFragment(ViewPort viewPort, SpriteBatch batch) {
		this.viewPort = viewPort;
		this.batch = batch;
		this.font = ResourceManager.instance().getFont("alegreya-sans-sc-black-20");
	}

	@Override
	public void render() {
		if (enabled) {
			batch.begin();
			int x = 10;
			int y = viewPort.height - 10;
			for (Console.LogLine log : GameState.console().getLog()) {
				log.color.a = Math.max((log.expiration - GameState.time()) / GameState.console().getMessageExpiration(), 0);
				font.setColor(log.color);
				font.draw(batch, log.message, x, y);
				y -= 16;
			}

			font.setColor(Color.WHITE);

			x = viewPort.width - 200;
			y = viewPort.height - 10;
			for (Map.Entry<String, Supplier<String>> watch : GameState.console().getWatches().entrySet()) {
				font.draw(batch, watch.getKey() + ": " + watch.getValue().get(), x, y);
				y -= 16;
			}
			batch.end();
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
