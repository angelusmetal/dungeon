package com.dungeon.engine.entity.factory;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

public interface NewEntityTypeFactory {
	Entity build(Vector2 origin);
}
