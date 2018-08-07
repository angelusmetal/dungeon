package com.dungeon.game.object.torch;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.resource.Resources;

public class TorchFactory implements EntityFactory.EntityTypeFactory {

	private final EntityPrototype prototype;

	public TorchFactory() {
		prototype = Resources.prototypes.get("torch");
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(prototype, origin);
	}
}
