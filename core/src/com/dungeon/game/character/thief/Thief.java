package com.dungeon.game.character.thief;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.CooldownTrigger;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class Thief extends PlayerCharacter {

	private final ThiefFactory factory;

	Thief(Vector2 origin, ThiefFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		setCurrentAnimation(getIdleAnimation());
		fireCooldown = new CooldownTrigger(0.2f);
	}

	@Override
	protected Entity createProjectile(Vector2 origin) {
		return factory.createBullet(origin);
	}

	@Override protected Animation<TextureRegion> getAttackAnimation() {
		return factory.attackAnimation;
	}

	@Override protected Animation<TextureRegion> getIdleAnimation() {
		return factory.idleAnimation;
	}

	@Override protected Animation<TextureRegion> getWalkAnimation() {
		return factory.walkAnimation;
	}

	@Override
	protected void onExpire() {
		super.onExpire();
		GameState.addEntity(GameState.build(EntityType.TOMBSTONE, getPos()));
	}

}
