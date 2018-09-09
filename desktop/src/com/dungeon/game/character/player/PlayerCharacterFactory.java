package com.dungeon.game.character.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.Resources;

public class PlayerCharacterFactory {

	public static final String ASSASSIN_WALK = "assassin_walk";
	private static final String ASSASSIN_ATTACK = "assassin_attack";
	private static final String ASSASSIN_IDLE = "assassin_idle";

	public static final String THIEF_WALK = "thief_walk";
	private static final String THIEF_ATTACK = "thief_attack";
	private static final String THIEF_IDLE = "thief_idle";

	public static final String WITCH_WALK = "witch_walk";
	private static final String WITCH_ATTACK = "witch_attack";
	private static final String WITCH_IDLE = "witch_idle";

	public Entity assassin(Vector2 origin, EntityPrototype prototype) {
		return factory(ASSASSIN_IDLE, ASSASSIN_WALK, ASSASSIN_ATTACK, prototype, origin);
	}

	public Entity thief(Vector2 origin, EntityPrototype prototype) {
		return factory(THIEF_IDLE, THIEF_WALK, THIEF_ATTACK, prototype, origin);
	}

	public Entity witch(Vector2 origin, EntityPrototype prototype) {
		return factory(WITCH_IDLE, WITCH_WALK, WITCH_ATTACK, prototype, origin);
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
