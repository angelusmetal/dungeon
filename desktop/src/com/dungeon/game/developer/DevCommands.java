package com.dungeon.game.developer;

import com.badlogic.gdx.Gdx;
import com.dungeon.engine.Engine;
import com.dungeon.engine.console.ConsoleVar;
import com.dungeon.game.Game;
import com.dungeon.game.player.Players;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DevCommands {
	private final DevTools devTools;

	public DevCommands(DevTools devTools) {
		this.devTools = devTools;

		// Add console commands
		Game.getConsole().bindExpression("play_music", this::playMusic);
		Game.getConsole().bindExpression("stop_music", this::stopMusic);
		Game.getConsole().bindExpression("say", this::say);
		Game.getConsole().bindExpression("spawn", this::spawn);

		// Add variables
		Game.getConsole().bindVar(ConsoleVar.mutableColor("e_baseLight", Engine::getBaseLight, Engine::setBaseLight));
		Game.getConsole().bindVar(ConsoleVar.readOnlyFloat("e_time", Engine::time));
	}

	public boolean playMusic(List<String> tokens, Consumer<String> output) {
		String path = tokens.get(0);
		Engine.audio.playMusic(Gdx.files.internal(path));
		return true;
	}

	public boolean stopMusic(List<String> tokens, Consumer<String> output) {
		Engine.audio.stopMusic();
		return true;
	}

	public boolean say(List<String> tokens, Consumer<String> output) {
		Players.get(0).getAvatar().say(String.join(" ", tokens));
		return true;
	}

	public boolean spawn(List<String> tokens, Consumer<String> output) {
		if (tokens.size() >= 1) {
			String type = tokens.get(0);
			try {
				Engine.entities.add(Game.build(type, devTools.mouseAt()));
			} catch (RuntimeException e) {
				output.accept(e.getMessage());
			}
		}
		return true;
	}


}
