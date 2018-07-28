package com.dungeon.game.level.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.util.Util;

/**
 * Placeholder for entities that have not yet been created.
 */
public class EntityPlaceholder {

	private final EntityType type;
	private final Vector2 origin;
	private final float chance;

	public EntityPlaceholder(EntityType type, Vector2 origin) {
		this(type, origin, 1f);
	}

	public EntityPlaceholder(EntityType type, Vector2 origin, float chance) {
		this.type = type;
		this.origin = origin;
		this.chance = Util.clamp(chance);
	}

	public EntityType getType() {
		return type;
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public float getChance() {
		return chance;
	}
}
