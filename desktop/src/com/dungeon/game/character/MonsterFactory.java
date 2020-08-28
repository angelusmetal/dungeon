package com.dungeon.game.character;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.character.ghost.Ghost;
import com.dungeon.game.character.slime.Slime;
import com.dungeon.game.character.slime.SlimeSpawn;

public class MonsterFactory {
	public Entity ghost(Vector2 origin, EntityPrototype prototype) {
		return new Ghost(origin, prototype);
	}

	public Entity slime(Vector2 origin, EntityPrototype prototype) {
		return new Slime(origin, prototype);
	}

	public Entity slimeSpawn(Vector2 origin, EntityPrototype prototype) {
		return new SlimeSpawn(origin, prototype);
	}
}
