package com.dungeon.engine.entity.factory;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

/**
 * This is for the runtime invocation, where the prototype is not provided.
 */
public interface EntityTypeFactory {
	Entity build(Vector2 origin);
}
