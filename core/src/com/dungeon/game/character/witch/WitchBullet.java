package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;

class WitchBullet extends Projectile {

	private final WitchFactory factory;

	public WitchBullet(WitchFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, new Vector2(6, 6)), startTime, factory.bulletPrototype);
		this.factory = factory;
		light = factory.bulletLight;
	}

	@Override
	protected Animation<TextureRegion> getAnimation(Vector2 direction) {
		if (Math.abs(getSelfMovement().x) > Math.abs(getSelfMovement().y)) {
			// Sideways animation
			return factory.bulletFlySideAnimation;
		} else {
			// North / south animation
			return getSelfMovement().y < 0 ? factory.bulletFlySouthAnimation : factory.bulletFlyNorthAnimation;
		}
	}

	@Override
	protected Animation<TextureRegion> getExplodeAnimation() {
		return factory.bulletExplodeAnimation;
	}
}
