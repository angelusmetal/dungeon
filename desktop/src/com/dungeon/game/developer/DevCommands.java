package com.dungeon.game.developer;

import com.badlogic.gdx.Gdx;
import com.dungeon.engine.Engine;
import com.dungeon.engine.console.Console;
import com.dungeon.engine.console.ConsoleOutput;
import com.dungeon.engine.console.ConsoleVar;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.Game;
import com.dungeon.game.combat.module.ModularWeapon;
import com.dungeon.game.combat.module.ModularWeaponGenerator;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.DungeonResources;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DevCommands {
	private final DevTools devTools;

	public DevCommands(DevTools devTools) {
		this.devTools = devTools;

		// Add console commands
		Game.getConsole().bindExpression("playMusic", this::playMusic);
		Game.getConsole().bindExpression("stopMusic", this::stopMusic);
		Game.getConsole().bindExpression("say", this::say);
		Game.getConsole().bindExpression("shout", this::shout);
		Game.getConsole().bindExpression("spawn", this::spawn, Game::knownEntityTypes);
		Game.getConsole().bindExpression("die", (args, output) -> {
			Players.all().forEach(p -> p.getAvatar().expire());
			return true;
		});
		Game.getConsole().bindExpression("exitLevel", (args, output) -> {
			Game.exitLevel();
			return true;
		});
		Game.getConsole().bindExpression("randomWeapon", this::randomWeapon);
		Game.getConsole().bindExpression("gold", this::gold);

		// Add variables
		Game.getConsole().bindVar(ConsoleVar.mutableColor("baseLight", Engine::getBaseLight, Engine::setBaseLight));
		Game.getConsole().bindVar(ConsoleVar.readOnlyFloat("time", Engine::time));
		Game.getConsole().bindVar(ConsoleVar.mutableInt("levelCount", Game::getLevelCount, Game::setLevelCount));
		Game.getConsole().bindVar(ConsoleVar.mutableFloat("musicVolume", Engine.audio::getMusicVolume, Engine.audio::setMusicVolume));
	}

	private boolean gold(List<String> tokens, ConsoleOutput output) {
		if (!tokens.isEmpty()) {
			int gold = Integer.parseInt(tokens.get(0));
			Players.all().forEach(player -> player.addGold(gold));
		}
		return true;
	}

	public boolean playMusic(List<String> tokens, ConsoleOutput output) {
		if (!tokens.isEmpty()) {
			String path = tokens.get(0);
			Engine.audio.playMusic(Gdx.files.internal(path));
		}
		return true;
	}

	public boolean stopMusic(List<String> tokens, ConsoleOutput output) {
		Engine.audio.stopMusic();
		return true;
	}

	public boolean say(List<String> tokens, ConsoleOutput output) {
		Players.get(0).getAvatar().say(String.join(" ", tokens));
		return true;
	}

	public boolean shout(List<String> tokens, ConsoleOutput output) {
		Game.shout(Players.get(0).getAvatar(), String.join(" ", tokens));
		return true;
	}

	public boolean spawn(List<String> tokens, ConsoleOutput output) {
		if (!tokens.isEmpty()) {
			String type = tokens.get(0);
			try {
				Engine.entities.add(Game.build(type, devTools.mouseAt()));
			} catch (RuntimeException e) {
				output.print(e.getMessage());
			}
		}
		return true;
	}

	private boolean randomWeapon(List<String> tokens, ConsoleOutput output) {
		if (!tokens.isEmpty()) {
			int score = Integer.parseInt(tokens.get(0));
			try {
				Engine.entities.add(
						new WeaponFactory().buildWeaponEntity(devTools.mouseAt(), DungeonResources.prototypes.get("weapon_cat_staff"), () -> new ModularWeaponGenerator().generate(score))
				);
			} catch (RuntimeException e) {
				output.print(e.getMessage());
			}
		}
		return true;
	}


}
