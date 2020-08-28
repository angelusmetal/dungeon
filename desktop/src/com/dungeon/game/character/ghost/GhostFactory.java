package com.dungeon.game.character.ghost;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.moandjiezana.toml.Toml;

public class GhostFactory {

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new Ghost(origin, prototype);
	}

}
