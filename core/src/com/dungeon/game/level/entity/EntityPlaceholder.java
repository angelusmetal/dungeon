package com.dungeon.game.level.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Placeholder for entities that have not yet been created.
 */
public class EntityPlaceholder {

	private final EntityType type;
	private final Vector2 origin;

	public EntityPlaceholder(EntityType type, Vector2 origin) {
		this.type = type;
		this.origin = origin;
	}

	public EntityType getType() {
		return type;
	}

	public Vector2 getOrigin() {
		return origin;
	}
}
