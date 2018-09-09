package com.dungeon.game.character.ghost;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.resource.Resources;
import com.moandjiezana.toml.Toml;

public class GhostFactory {

	final float maxTargetDistance;
	final float visibleTime;
	final float visibleSpeed;
	final float stealthSpeed;
	final float damagePerSecond;

	public GhostFactory() {
		Toml config = ConfigUtil.getTomlMap(Game.getConfiguration(), "creatures", "id").get("GHOST");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		visibleTime = config.getDouble("visibleTime", 2d).floatValue();
		visibleSpeed = config.getLong("visibleSpeed", 20L).floatValue();
		stealthSpeed = config.getLong("stealthSpeed", 40L).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 20L).floatValue();
	}

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new Ghost(origin, prototype, this);
	}

}
