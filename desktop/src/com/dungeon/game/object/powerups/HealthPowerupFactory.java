package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;

public class HealthPowerupFactory implements EntityTypeFactory {

	private final EntityPrototype powerupPrototype;

	public HealthPowerupFactory() {
		powerupPrototype = Resources.prototypes.get("health_powerup");
	}

	@Override
	public Entity build(Vector2 origin) {
		return new DungeonEntity(powerupPrototype, origin) {

			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					int amount = 25;
					character.heal(amount);
					expire();
					character.getPlayer().getConsole().log("Healed for " + amount, Color.GOLD);
//					character.getPlayer().getRenderer().beginMotionBlur();
					return true;
				} else {
					return false;
				}
			}

		};
	}
}
