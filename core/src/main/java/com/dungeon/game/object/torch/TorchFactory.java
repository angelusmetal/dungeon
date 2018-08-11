package com.dungeon.game.object.torch;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.NewEntityTypeFactory;
import com.dungeon.game.resource.Resources;

public class TorchFactory implements NewEntityTypeFactory {

	private final EntityPrototype prototype;

	public TorchFactory() {
		prototype = Resources.prototypes.get("torch");
	}

	@Override
	public Entity build(Vector2 origin) {
		Entity entity = new Entity(prototype, origin);
		// TODO Move this logic to toml files
		entity.setZPos(24);
		return entity;
	}
}
