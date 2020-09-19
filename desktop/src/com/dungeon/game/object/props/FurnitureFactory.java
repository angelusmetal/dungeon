package com.dungeon.game.object.props;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.ShadowType;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.ui.ParticleBuilder;

import java.util.Arrays;
import java.util.List;

public class FurnitureFactory {

	Sound pickupSound = Resources.sounds.get("audio/sound/pickup.ogg");

	public Entity doorHorizontal(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public void onSignal(Entity emitter) {
				canBlock = false;
				canBeHit = false;
				shadowType = ShadowType.NONE;
			}
		};
	}

	public Entity doorVertical(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public void onSignal(Entity emitter) {
				canBlock = false;
				canBeHit = false;
				shadowType = ShadowType.NONE;
			}
		};
	}

	public Entity coin(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					Engine.audio.playSound(pickupSound, character.getOrigin(), 1f, 0.05f);
					Vector2 destination = character.getPlayer().getRenderer().getHud().getHudWidget(character.getPlayer()).getCoinCenter();
					ParticleBuilder.of(this, (PlayerEntity) entity, destination)
							.animation(getAnimation())
							.sparksPerSecond(30f)
							.sparkDuration(0.25f)
							.sparkAttenuation(0.25f)
							.endAction(() -> character.getPlayer().addGold(1))
							.add();
					expire();
					return true;
				}
				return false;
			}
		};
	}

	private static final List<String> dummyPhrases = Arrays.asList("Ouch", "You should be proud of yourself");

	public Entity dummy(Vector2 origin, EntityPrototype prototype) {
		return new CreatureEntity(origin, prototype) {
			@Override public void onHit() {
				super.onHit();
				health = maxHealth;
				if (Rand.chance(0.1f)) {
					shout(dummyPhrases);
				}
			}
		};
	}

}
