package com.dungeon.game.developer;

import com.dungeon.engine.Engine;
import com.dungeon.engine.console.ConsoleVar;
import com.dungeon.game.Game;
import com.dungeon.game.combat.module.ModularWeaponGenerator;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.DungeonResources;

import java.util.List;

public class DevCommands {
	private final DevTools devTools;

	public DevCommands(DevTools devTools) {
		this.devTools = devTools;

		// Add console commands
		Engine.console.bindExpression("say", this::say);
		Engine.console.bindExpression("shout", this::shout);
		Engine.console.bindExpression("spawn", this::spawn, Game::knownEntityTypes);
		Engine.console.bindExpression("die", args -> {
			Players.all().forEach(p -> p.getAvatar().expire());
			return true;
		});
		Engine.console.bindExpression("exitLevel", args -> {
			Game.exitLevel();
			return true;
		});
		Engine.console.bindExpression("randomWeapon", this::randomWeapon);
		Engine.console.bindExpression("gold", this::gold);

		// Add variables
		Engine.console.bindVar(ConsoleVar.mutableInt("levelCount", Game::getLevelCount, Game::setLevelCount));
	}

	private boolean gold(List<String> tokens) {
		if (!tokens.isEmpty()) {
			int gold = Integer.parseInt(tokens.get(0));
			Players.all().forEach(player -> player.addGold(gold));
		}
		return true;
	}

	public boolean say(List<String> tokens) {
		Players.get(0).getAvatar().say(String.join(" ", tokens));
		return true;
	}

	public boolean shout(List<String> tokens) {
		Game.shout(Players.get(0).getAvatar(), String.join(" ", tokens));
		return true;
	}

	public boolean spawn(List<String> tokens) {
		if (!tokens.isEmpty()) {
			String type = tokens.get(0);
			try {
				Engine.entities.add(Game.build(type, devTools.mouseAt()));
			} catch (RuntimeException e) {
				Engine.console.getOutput().print(e.getMessage());
			}
		}
		return true;
	}

	private boolean randomWeapon(List<String> tokens) {
		if (!tokens.isEmpty()) {
			int score = Integer.parseInt(tokens.get(0));
			try {
				Engine.entities.add(
						new WeaponFactory().buildWeaponEntity(devTools.mouseAt(), DungeonResources.prototypes.get("random_weapon"), () -> new ModularWeaponGenerator().generate(score))
				);
			} catch (RuntimeException e) {
				Engine.console.getOutput().print(e.getMessage());
			}
		}
		return true;
	}


}
