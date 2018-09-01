package com.dungeon.game.object.exit;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.game.resource.Resources;
import com.dungeon.engine.Engine;

public class ExitPlatformFactory implements EntityTypeFactory {

	private final EntityPrototype prototype;

	public ExitPlatformFactory() {
		prototype = Resources.prototypes.get("exit_platform");

	}

	@Override
	public Entity build(Vector2 origin) {
		return new DungeonEntity(prototype, origin) {
			boolean exited = false;
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!exited && entity instanceof PlayerEntity) {
					exited = true;
					Game.exitLevel();
				}
				return true;
			}
		};
	}

}
