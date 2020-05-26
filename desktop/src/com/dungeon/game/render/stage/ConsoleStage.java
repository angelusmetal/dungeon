package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.OldConsole;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.resource.Resources;

import java.util.Map;
import java.util.function.Supplier;

public class ConsoleStage implements Renderer {

	private final ViewPort viewPort;
	private final SpriteBatch batch;
	private final BitmapFont font;
	private final OldConsole console;

	public ConsoleStage(ViewPort viewPort, SpriteBatch batch, OldConsole console) {
		this.viewPort = viewPort;
		this.batch = batch;
		this.font = Resources.fonts.get("alegreya-sans-sc-black-15");
		this.console = console;
	}

	@Override
	public void render() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width, viewPort.height);
		batch.begin();
		int x = viewPort.posX + 10;
		int y = viewPort.posY + 10 + 16;
		if (Game.displayConsole()) {
			font.setColor(Color.WHITE);
			String cursor = (int) (Engine.time() * 2f) % 2 == 0 ? "|" : "";
			font.draw(batch, "> " + Game.getCommandConsole().getCurrentCommand() + cursor, x, y);
		}

		x = viewPort.posX + 10;
		y = viewPort.posY + 10 + 32;
		for (OldConsole.LogLine log : console.getLog()) {
			log.color.a = Math.max((log.expiration - Engine.time()) / console.getMessageExpiration(), 0);
			font.setColor(log.color);
			font.draw(batch, log.message, x, y);
			y += 16;
		}

		font.setColor(Color.WHITE);

		x = viewPort.posX + viewPort.width - 300;
		y = viewPort.posY + viewPort.height - 10;
		for (Map.Entry<String, Supplier<String>> watch : console.getWatches().entrySet()) {
			font.draw(batch, watch.getKey() + ": " + watch.getValue().get(), x, y);
			y -= 16;
		}
		batch.end();
	}

	@Override
	public void dispose() {}

}
