package com.dungeon.engine.entity.factory;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;

/**
 * This is to be implemented by factory methods, where the prototype is provided.
 */
public interface EntityPrototypeFactory {
	Entity build(Vector2 origin, EntityPrototype prototype);
}
