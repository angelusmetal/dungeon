package com.dungeon.game.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.resource.ResourceManager;

import java.util.function.Predicate;

public class SwordWeapon extends MeleeWeapon {

	private static final String SLASH_ANIMATION = "melee_slash";

	public SwordWeapon() {
		super("Sword", () -> 50f, DamageType.NORMAL, 150f);
	}

	@Override
	protected Animation<TextureRegion> getAttackAnimation() {
		return ResourceManager.getAnimation(SLASH_ANIMATION);
	}

	@Override
	protected Vector2 getHitBox() {
		return new Vector2(20, 20);
	}

	@Override
	protected Predicate<Entity> getHitPredicate() {
		return PlayerEntity.HIT_NON_PLAYERS;
	}

	@Override
	protected float getSpawnDistance() {
		return 20;
	}

}
