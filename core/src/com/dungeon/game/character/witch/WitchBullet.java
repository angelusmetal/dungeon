package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

class WitchBullet extends Projectile {

	private final WitchFactory factory;

	public WitchBullet(WitchFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, new Vector2(6, 6)), startTime, factory.bullet);
		this.factory = factory;
		light = factory.bulletLight;
		setCurrentAnimation(new GameAnimation(getAnimation(getSelfMovement()), startTime));
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
	protected Particle createExplosion(GameState state, Vector2 origin) {
		return new Explosion(factory, origin, state.getStateTime());
	}

	public static class Explosion extends Particle {

		private final WitchFactory factory;

		public Explosion(WitchFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, new Vector2(6, 6)), startTime, factory.bulletExplosion);
			this.factory = factory;
			light = factory.bulletLight;
			setCurrentAnimation(new GameAnimation(factory.bulletExplodeAnimation, startTime));
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.bulletExplodeAnimation;
		}

	}
}
