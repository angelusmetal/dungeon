package com.dungeon.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
		this.font = ResourceManager.getFont("alegreya-sans-sc-black-15");
	}

	@Override
	public void render() {
		if (enabled) {
			batch.begin();
			int x = viewPort.posX + 10;
			int y = viewPort.posY + viewPort.height - 10;
			for (Console.LogLine log : GameState.console().getLog()) {
				log.color.a = Math.max((log.expiration - GameState.time()) / GameState.console().getMessageExpiration(), 0);
				font.setColor(log.color);
				font.draw(batch, log.message, x, y);
				y -= 16;
			}

			font.setColor(Color.WHITE);

			x = viewPort.posX + viewPort.width - 200;
			y = viewPort.posY + viewPort.height - 10;
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

	@Override
	public void dispose() {}

}
