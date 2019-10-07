package com.dungeon.game.character.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.Resources;

public class PlayerCharacterFactory {

	public static final String MORT_WALK = "mort_walk";
	private static final String MORT_ATTACK = "mort_attack";
	private static final String MORT_IDLE = "mort_idle";

	public static final String JACK_WALK = "jack_walk";
	private static final String JACK_ATTACK = "jack_attack";
	private static final String JACK_IDLE = "jack_idle";

	public static final String KARA_WALK = "kara_walk";
	private static final String KARA_ATTACK = "kara_attack";
	private static final String KARA_IDLE = "kara_idle";

	public Entity mort(Vector2 origin, EntityPrototype prototype) {
		return factory(MORT_IDLE, MORT_WALK, MORT_ATTACK, prototype, origin);
	}

	public Entity jack(Vector2 origin, EntityPrototype prototype) {
		return factory(JACK_IDLE, JACK_WALK, JACK_ATTACK, prototype, origin);
	}

	public Entity kara(Vector2 origin, EntityPrototype prototype) {
		return factory(KARA_IDLE, KARA_WALK, KARA_ATTACK, prototype, origin);
	}

	private Entity factory(String idle, String walk, String attack, EntityPrototype prototype, Vector2 origin) {
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
		};

	}


}
