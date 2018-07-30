package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class Assassin extends PlayerEntity {

	private final AssassinFactory factory;

	Assassin(Vector2 origin, AssassinFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		setCurrentAnimation(factory.idleAnimation);
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
		GameState.addEntity(GameState.build(EntityType.TOMBSTONE, getOrigin()));
	}

}
