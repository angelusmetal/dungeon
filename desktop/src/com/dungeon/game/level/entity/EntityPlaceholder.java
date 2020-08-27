package com.dungeon.game.level.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.LightPrototype;
import com.dungeon.engine.util.Util;

/**
 * Placeholder for entities that have not yet been created.
 */
public class EntityPlaceholder {

	private final String type;
	private final Vector2 origin;
	private final Float z;
	private final float chance;
	private final LightPrototype lightPrototype;

	public EntityPlaceholder(String type, Vector2 origin) {
		this(type, origin, null, 1f, null);
	}

	public EntityPlaceholder(String type, Vector2 origin, Float z, float chance, LightPrototype lightPrototype) {
		this.type = type;
		this.origin = origin;
		this.z = z;
		this.chance = Util.clamp(chance);
		this.lightPrototype = lightPrototype;
	}

	public EntityPlaceholder relativeTo(float x, float y) {
		return new EntityPlaceholder(type, origin.cpy().add(x, y), z, chance, lightPrototype);
	}

	public String getType() {
		return type;
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public Float getZ() {
		return z;
	}

	public float getChance() {
		return chance;
	}

	public LightPrototype getLightPrototype() {
		return lightPrototype;
	}
}
