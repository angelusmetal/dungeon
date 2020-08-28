package com.dungeon.game.character.slime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;

public class SlimeFactory {

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new Slime(origin, prototype);
	}

	public Entity buildSpawn(Vector2 origin, EntityPrototype prototype) {
		return new SlimeSpawn(origin, prototype);
	}

}
