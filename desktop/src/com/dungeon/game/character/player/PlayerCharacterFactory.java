package com.dungeon.game.character.player;

import com.badlogic.gdx.graphics.g2d.Animation;
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

	public static final String JACK_WALK = "jack_walk";
	private static final String JACK_ATTACK = "jack_attack";
	private static final String JACK_IDLE = "jack_idle";

	public static final String KARA_WALK = "kara_walk";
	private static final String KARA_ATTACK = "kara_attack";
	private static final String KARA_IDLE = "kara_idle";

	public Entity alma(Vector2 origin, EntityPrototype prototype) {
		final EntityPrototype dust_cloud_blood = DungeonResources.prototypes.get("dust_cloud_blood");
		Function<Entity, Metronome> stepFactory = e -> new Metronome(0.2f, () -> Engine.entities.add(new Entity(dust_cloud_blood, e.getOrigin().cpy().add(0, 1))));
		return factory(ALMA_IDLE, ALMA_WALK, ALMA_ATTACK, stepFactory, prototype, origin);
	}

	public Entity mort(Vector2 origin, EntityPrototype prototype) {
		final EntityPrototype[] dust_clouds = {DungeonResources.prototypes.get("dust_cloud_1"), DungeonResources.prototypes.get("dust_cloud_2"), DungeonResources.prototypes.get("dust_cloud_3")};
		Function<Entity, Metronome> stepFactory = e -> new Metronome(0.4f, () -> Engine.entities.add(new Entity(Rand.pick(dust_clouds), e.getOrigin().cpy().add(0, 1))));
		return factory(MORT_IDLE, MORT_WALK, MORT_ATTACK, stepFactory, prototype, origin);
	}

	public Entity jack(Vector2 origin, EntityPrototype prototype) {
		final EntityPrototype[] dust_clouds = {DungeonResources.prototypes.get("dust_cloud_1"), DungeonResources.prototypes.get("dust_cloud_2"), DungeonResources.prototypes.get("dust_cloud_3")};
		Function<Entity, Metronome> stepFactory = e -> new Metronome(0.4f, () -> Engine.entities.add(new Entity(Rand.pick(dust_clouds), e.getOrigin().cpy().add(0, 1))));
		return factory(JACK_IDLE, JACK_WALK, JACK_ATTACK, stepFactory, prototype, origin);
	}

	public Entity kara(Vector2 origin, EntityPrototype prototype) {
		final EntityPrototype[] dust_clouds = {DungeonResources.prototypes.get("dust_cloud_1"), DungeonResources.prototypes.get("dust_cloud_2"), DungeonResources.prototypes.get("dust_cloud_3")};
		Function<Entity, Metronome> stepFactory = e -> new Metronome(0.4f, () -> Engine.entities.add(new Entity(Rand.pick(dust_clouds), e.getOrigin().cpy().add(0, 1))));
		return factory(KARA_IDLE, KARA_WALK, KARA_ATTACK, stepFactory, prototype, origin);
	}

	private Entity factory(String idle, String walk, String attack, Function<Entity, Metronome> stepMetronomeFactory, EntityPrototype prototype, Vector2 origin) {
		final Animation<TextureRegion> idleAnimation = Resources.animations.get(idle);
		final Animation<TextureRegion> walkAnimation = Resources.animations.get(walk);
		final Animation<TextureRegion> attackAnimation = Resources.animations.get(attack);

		return new PlayerEntity(prototype, origin) {
			@Override protected Animation<TextureRegion> getAttackAnimation() { return attackAnimation; }
			@Override protected Animation<TextureRegion> getIdleAnimation() { return idleAnimation; }
			@Override protected Animation<TextureRegion> getWalkAnimation() { return walkAnimation; }
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
