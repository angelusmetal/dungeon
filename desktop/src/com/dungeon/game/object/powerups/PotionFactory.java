package com.dungeon.game.object.powerups;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.ui.ParticleBuilder;

public class PotionFactory {

	Sound pickupSound = Resources.sounds.get("audio/sound/potion.ogg");
	Animation<Material> sparkle = Resources.animations.get("particle_sparkle");

	public Entity healthSmall(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					Engine.audio.playSound(pickupSound, character.getOrigin(), 1f, 0.05f);
					createHeal(this, (PlayerEntity) entity, character, 50, 2, 1, 1);
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
					Engine.audio.playSound(pickupSound, character.getOrigin(), 1f, 0.05f);
					createHeal(this, (PlayerEntity) entity, character, 150, 2, 1, 1);
					expire();
					return true;
				} else {
					return false;
				}
			}

		};
	}
	private void createHeal(Entity emitter, PlayerEntity entity, PlayerEntity character, int amount, int step, float delay, float duration) {
		Vector2 destination = character.getPlayer().getRenderer().getHud().getHudWidget(character.getPlayer()).getHealthCenter();
		ParticleBuilder particleBuilder = ParticleBuilder.of(emitter, entity, destination)
				.animation(sparkle)
				.sparkColor(Color.RED)
				.sparksPerSecond(10f)
				.sparkDuration(0.5f)
				.endAction(() -> character.heal(step));
		float time=  delay;
		float timeStep = duration / ((float)amount / step);
		for (int i = 0; i < amount; i += step) {
			particleBuilder.add().duration(time);
			time += timeStep;
		}
	}
}
