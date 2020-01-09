package com.dungeon.game.level.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.LightPrototype;
import com.dungeon.engine.util.Util;

import java.util.Optional;

/**
 * Placeholder for entities that have not yet been created.
 */
public class EntityPlaceholder {

	private final String type;
	private final Vector2 origin;
	private final float chance;
	private final LightPrototype lightPrototype;

	public EntityPlaceholder(String type, Vector2 origin) {
		this(type, origin, 1f, null);
	}

	public EntityPlaceholder(String type, Vector2 origin, float chance) {
		this(type, origin, chance, null);
	}

	public EntityPlaceholder(String type, Vector2 origin, float chance, LightPrototype lightPrototype) {
		this.type = type;
		this.origin = origin;
		this.chance = Util.clamp(chance);
		this.lightPrototype = lightPrototype;
	}

	public String getType() {
		return type;
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public float getChance() {
		return chance;
	}

	public LightPrototype getLightPrototype() {
		return lightPrototype;
	}
}
