package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class WitchBullet extends Projectile {

	private static final Vector2 BOUNDING_BOX = new Vector2(6, 6);
	private static final Vector2 DRAW_OFFSET = new Vector2(12, 12);

	private final WitchFactory factory;

	public WitchBullet(WitchFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bullet);
		this.factory = factory;
		light = factory.bulletLight;
		setCurrentAnimation(new GameAnimation(getAnimation(getSelfImpulse()), startTime));
	}

	@Override
	protected Animation<TextureRegion> getAnimation(Vector2 direction) {
		if (Math.abs(getSelfImpulse().x) > Math.abs(getSelfImpulse().y)) {
			// Sideways animation
			return factory.bulletFlySideAnimation;
		} else {
			// North / south animation
			return getSelfImpulse().y < 0 ? factory.bulletFlySouthAnimation : factory.bulletFlyNorthAnimation;
		}
	}

	@Override
	protected Particle createExplosion(GameState state, Vector2 origin) {
		return new Explosion(factory, origin, state.getStateTime());
	}

	@Override
	protected Particle createTrail(GameState state, Vector2 origin) {
		return new Trail(factory, origin, state.getStateTime());
	}

	public static class Explosion extends Particle {

		// TODO Maybe remove this one when all are in a single sheet
		private static final Vector2 DRAW_OFFSET = new Vector2(4, 4);

		private final WitchFactory factory;

		public Explosion(WitchFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bulletExplosion);
			this.factory = factory;
			light = factory.bulletLight;
			setCurrentAnimation(new GameAnimation(factory.bulletExplodeAnimation, startTime));
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.bulletExplodeAnimation;
		}

	}

	public static class Trail extends Particle {

		// TODO Maybe remove this one when all are in a single sheet
		private static final Vector2 DRAW_OFFSET = new Vector2(4, 4);

		private final WitchFactory factory;

		public Trail(WitchFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bulletTrail);
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
