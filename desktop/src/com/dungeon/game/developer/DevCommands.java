package com.dungeon.game.developer;

import com.badlogic.gdx.Gdx;
import com.dungeon.engine.Engine;
import com.dungeon.engine.console.ConsoleVar;
import com.dungeon.game.Game;
import com.dungeon.game.player.Players;

import java.util.List;
import java.util.stream.Collectors;

public class DevCommands {
	private final DevTools devTools;

	public DevCommands(DevTools devTools) {
		this.devTools = devTools;

		// Add console commands
		Game.getCommandConsole().bindCommand("play_music", this::playMusic);
		Game.getCommandConsole().bindCommand("stop_music", this::stopMusic);
		Game.getCommandConsole().bindCommand("say", this::say);
		Game.getCommandConsole().bindCommand("spawn", this::spawn);

		// Add variables
		Game.getCommandConsole().bindVar(ConsoleVar.mutableColor("e_baseLight", Engine::getBaseLight, Engine::setBaseLight));
		Game.getCommandConsole().bindVar(ConsoleVar.readOnlyFloat("e_time", Engine::time));
	}

	public void playMusic(List<String> tokens) {
		String path = tokens.get(1);
		Engine.audio.playMusic(Gdx.files.internal(path));
	}

	public void stopMusic(List<String> tokens) {
		Engine.audio.stopMusic();
	}

	public void say(List<String> tokens) {
		Players.get(0).getAvatar().say(tokens.stream().skip(1).collect(Collectors.joining(" ")));
	}

	public void spawn(List<String> tokens) {
		if (tokens.size() < 2) {
			return;
		}
		String type = tokens.get(1);
		try {
			Engine.entities.add(Game.build(type, devTools.mouseAt()));
		} catch (RuntimeException e) {
			Game.getCommandConsole().print(e.getMessage());
		}
	}


}
