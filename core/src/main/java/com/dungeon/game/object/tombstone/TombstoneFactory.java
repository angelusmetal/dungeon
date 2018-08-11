package com.dungeon.game.object.tombstone;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.NewEntityTypeFactory;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;

public class TombstoneFactory implements NewEntityTypeFactory {

	private final EntityPrototype prototype;

	public TombstoneFactory() {
		prototype = Resources.prototypes.get("tombstone");
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(prototype, origin) {
			@Override
			public void onHit() {
				Rand.doBetween(2, 5, () ->
						GameState.entities.add(GameState.build(EntityType.STONE_PARTICLE, getOrigin())));
			}
		};
	}
}
