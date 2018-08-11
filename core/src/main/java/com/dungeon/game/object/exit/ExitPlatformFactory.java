package com.dungeon.game.object.exit;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;

public class ExitPlatformFactory implements EntityTypeFactory {

	private final EntityPrototype prototype;

	public ExitPlatformFactory() {
		prototype = Resources.prototypes.get("exit_platform");

	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(prototype, origin) {
			boolean exited = false;
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!exited && entity instanceof PlayerEntity) {
					exited = true;
					GameState.exitLevel();
				}
				return true;
			}
		};
	}

}
