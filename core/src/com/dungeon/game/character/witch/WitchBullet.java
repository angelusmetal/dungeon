package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.game.state.GameState;

class WitchBullet extends Projectile {

	private static final Vector2 BOUNDING_BOX = new Vector2(6, 6);

	private final WitchFactory factory;

	public WitchBullet(WitchFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, BOUNDING_BOX), startTime, factory.bullet);
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

		private final WitchFactory factory;

		public Explosion(WitchFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), startTime, factory.bulletExplosion);
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

		private final WitchFactory factory;
		private final Color color;

		public Trail(WitchFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), startTime, factory.bulletTrail);
			this.factory = factory;
			light = factory.bulletLight;
			setCurrentAnimation(new GameAnimation(factory.bulletExplodeAnimation, startTime));
			color = new Color(1, 1, 1, 1f);
			drawContext = new ColorContext(color);
		}

		@Override
		public void think(GameState state) {
			super.think(state);
			color.a = startTime + timeToLive - state.getStateTime();
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.bulletExplodeAnimation;
		}

	}
}
