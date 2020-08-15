package com.dungeon.game.object.props;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.ShadowType;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.ui.particle.LinearParticle;
import com.dungeon.engine.ui.particle.PathParticle;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.TimeGradient;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.object.powerups.PotionFactory;
import com.dungeon.game.player.Player;
import com.dungeon.game.ui.ParticleBuilder;

import static com.dungeon.engine.util.Util.randVect;

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

}
