package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

public class HealthPowerupFactory {

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {

			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					int amount = 50;
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
