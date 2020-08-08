package com.dungeon.game.character.player;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.DungeonResources;

import java.util.function.Function;

public class PlayerCharacterFactory {

	public static final String ALMA_WALK = "alma_walk";
	private static final String ALMA_ATTACK = "alma_attack";
	private static final String ALMA_IDLE = "alma_idle";

	public static final String MORT_WALK = "mort_walk";
	private static final String MORT_ATTACK = "mort_attack";
	private static final String MORT_IDLE = "mort_idle";

	public static final String JACK_WALK = "jack_walk_right";
	public static final String KARA_WALK = "kara_walk_right";

	public static final float STEP_INTERVAL = 0.4f;

	private Function<Entity, Metronome> stepFactory;

	public PlayerCharacterFactory() {
		final EntityPrototype[] dust_clouds = {
				DungeonResources.prototypes.get("dust_cloud_1"),
				DungeonResources.prototypes.get("dust_cloud_2"),
				DungeonResources.prototypes.get("dust_cloud_3")
		};
		final Sound[] sounds = {
				Resources.sounds.get("audio/sound/step_1.ogg"),
				Resources.sounds.get("audio/sound/step_2.ogg")
		};
		stepFactory = e -> new Metronome(STEP_INTERVAL, () -> {
			Engine.audio.playSound(Rand.pick(sounds), e.getOrigin(), 0.5f, 0.05f);
			Engine.entities.add(new Entity(Rand.pick(dust_clouds), e.getOrigin().cpy().add(0, 1)));
		});
	}

	public Entity alma(Vector2 origin, EntityPrototype prototype) {
		final EntityPrototype dust_cloud_blood = DungeonResources.prototypes.get("dust_cloud_blood");
		Function<Entity, Metronome> stepFactory = e -> new Metronome(0.2f, () -> Engine.entities.add(new Entity(dust_cloud_blood, e.getOrigin().cpy().add(0, 1))));
		return factory(
				ALMA_IDLE,
				ALMA_WALK,
				ALMA_ATTACK,
				ALMA_IDLE,
				ALMA_WALK,
				ALMA_ATTACK,
				ALMA_IDLE,
				ALMA_WALK,
				ALMA_ATTACK,
				stepFactory,
				prototype,
				origin);
	}

	public Entity mort(Vector2 origin, EntityPrototype prototype) {
		return factory(
				MORT_IDLE,
				MORT_WALK,
				MORT_ATTACK,
				MORT_IDLE,
				MORT_WALK,
				MORT_ATTACK,
				MORT_IDLE,
				MORT_WALK,
				MORT_ATTACK,
				stepFactory,
				prototype,
				origin);
	}

	public Entity jack(Vector2 origin, EntityPrototype prototype) {
		return factory(
				"jack_idle_right",
				"jack_walk_right",
				"jack_attack_right",
				"jack_idle_down",
				"jack_walk_down",
				"jack_attack_down",
				"jack_idle_up",
				"jack_walk_up",
				"jack_attack_up",
				stepFactory,
				prototype,
				origin);
	}

	public Entity kara(Vector2 origin, EntityPrototype prototype) {
		return factory(
				"kara_idle_right",
				"kara_walk_right",
				"kara_attack_right",
				"kara_idle_down",
				"kara_walk_down",
				"kara_attack_down",
				"kara_idle_up",
				"kara_walk_up",
				"kara_attack_up",
				stepFactory,
				prototype,
				origin);
	}

	private Entity factory(String idleRight,
						   String walkRight,
						   String attackRight,
						   String idleDown,
						   String walkDown,
						   String attackDown,
						   String idleUp,
						   String walkUp,
						   String attackUp,
						   Function<Entity, Metronome> stepMetronomeFactory,
						   EntityPrototype prototype,
						   Vector2 origin) {
		final Animation<Sprite> idleRightAnimation = Resources.animations.get(idleRight);
		final Animation<Sprite> walkRightAnimation = Resources.animations.get(walkRight);
		final Animation<Sprite> attackRightAnimation = Resources.animations.get(attackRight);
		final Animation<Sprite> idleDownAnimation = Resources.animations.get(idleDown);
		final Animation<Sprite> walkDownAnimation = Resources.animations.get(walkDown);
		final Animation<Sprite> attackDownAnimation = Resources.animations.get(attackDown);
		final Animation<Sprite> idleUpAnimation = Resources.animations.get(idleUp);
		final Animation<Sprite> walkUpAnimation = Resources.animations.get(walkUp);
		final Animation<Sprite> attackUpAnimation = Resources.animations.get(attackUp);

		return new PlayerEntity(prototype, origin) {
			@Override protected Animation<Sprite> getAttackAnimation(PovDirection direction) {
				if (direction == PovDirection.south) {
					return attackDownAnimation;
				} else if (direction == PovDirection.north) {
					return attackUpAnimation;
				} else {
					return attackRightAnimation;
				}
			}
			@Override protected Animation<Sprite> getIdleAnimation(PovDirection direction) {
				if (direction == PovDirection.south) {
					return idleDownAnimation;
				} else if (direction == PovDirection.north) {
					return idleUpAnimation;
				} else {
					return idleRightAnimation;
				}
			}
			@Override protected Animation<Sprite> getWalkAnimation(PovDirection direction) {
				if (direction == PovDirection.south) {
					return walkDownAnimation;
				} else if (direction == PovDirection.north) {
					return walkUpAnimation;
				} else {
					return walkRightAnimation;
				}
			}
			@Override protected boolean isAttackAnimation() {
				return getAnimation() == attackDownAnimation ||
						getAnimation() == attackUpAnimation ||
						getAnimation() == attackRightAnimation;
			}
			@Override protected void onExpire() {
				super.onExpire();
				Engine.entities.add(Game.build(EntityType.TOMBSTONE, getOrigin()));
			}
			@Override protected Metronome getStepMetronome() {
				return stepMetronomeFactory.apply(this);
			}
		};

	}


}
