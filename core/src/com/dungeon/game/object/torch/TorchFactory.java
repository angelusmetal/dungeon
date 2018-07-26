package com.dungeon.game.object.torch;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;

public class TorchFactory implements EntityFactory.EntityTypeFactory {

	private final EntityPrototype prototype;

	public TorchFactory() {
		prototype = ResourceManager.getPrototype("torch");
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(origin, prototype) {
			@Override
			// TODO Make this a property
			public boolean isSolid() {
				return false;
			}
		};
	}
}
