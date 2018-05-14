package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.state.GameState;

public class Assassin extends PlayerCharacter {

	private final AssassinFactory factory;

	Assassin(Vector2 origin, AssassinFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		setCurrentAnimation(factory.idleAnimation);
		health = 100f;
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
		GameState.addEntity(factory.tombstoneSpawner.apply(getPos()));
	}

}
