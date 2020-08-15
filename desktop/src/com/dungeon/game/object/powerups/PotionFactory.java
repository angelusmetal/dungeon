package com.dungeon.game.object.powerups;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.ui.ParticleBuilder;

public class PotionFactory {

	Sound pickupSound = Resources.sounds.get("audio/sound/potion.ogg");
	Animation<Sprite> sparkle = Resources.animations.get("particle_sparkle");

	public Entity healthSmall(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					int amount = 50;
					Engine.audio.playSound(pickupSound, character.getOrigin(), 1f, 0.05f);
					Vector2 destination = character.getPlayer().getRenderer().getHud().getHudWidget(character.getPlayer()).getHealthCenter();
					ParticleBuilder.of(this, (PlayerEntity) entity, destination)
							.animation(sparkle)
							.sparkColor(Color.RED)
							.sparksPerSecond(60f)
							.sparkDuration(0.5f)
							.endAction(() -> character.heal(amount))
							.add();
					expire();
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
					Engine.audio.playSound(pickupSound, character.getOrigin(), 1f, 0.05f);
					Vector2 destination = character.getPlayer().getRenderer().getHud().getHudWidget(character.getPlayer()).getHealthCenter();
					ParticleBuilder.of(this, (PlayerEntity) entity, destination)
							.animation(sparkle)
							.sparkColor(Color.RED)
							.sparksPerSecond(60f)
							.sparkDuration(0.5f)
							.endAction(() -> character.heal(amount))
							.add();
					expire();
					return true;
				} else {
					return false;
				}
			}

		};
	}
}
