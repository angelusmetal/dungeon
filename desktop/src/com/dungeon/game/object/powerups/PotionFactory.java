package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

public class PotionFactory {

	public Entity healthSmall(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					int amount = 50;
					character.heal(amount);
					expire();
					character.getPlayer().getConsole().log("Healed for " + amount, Color.GOLD);
					return true;
				} else {
					return false;
				}
			}

		};
	}
	public Entity healthLarge(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					int amount = 150;
					character.heal(amount);
					expire();
					character.getPlayer().getConsole().log("Healed for " + amount, Color.GOLD);
					return true;
				} else {
					return false;
				}
			}

		};
	}
}
