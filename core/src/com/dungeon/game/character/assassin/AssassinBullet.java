package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

class AssassinBullet extends Projectile {

	private static final Vector2 BOUNDING_BOX = new Vector2(6, 6);
	private static final Vector2 DRAW_OFFSET = new Vector2(4, 4);
	private final AssassinFactory factory;

	public AssassinBullet(AssassinFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin,BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bullet);
		this.factory = factory;
		light = factory.bulletLight;
		setCurrentAnimation(new GameAnimation(getAnimation(getSelfImpulse()), startTime));
	}

	@Override
	protected Animation<TextureRegion> getAnimation(Vector2 direction) {
		return factory.bulletFlyAnimation;
	}

	@Override
	protected Particle createExplosion(GameState state, Vector2 origin) {
		return new Explosion(factory, getPos(), state.getStateTime());
	}

	@Override
	protected Particle createTrail(GameState state, Vector2 origin) {
		return new Trail(factory, getPos(), state.getStateTime());
	}

	class Explosion extends Particle {

		private final AssassinFactory factory;

		public Explosion(AssassinFactory factory, Vector2 origin, float startTime) {
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

		private final AssassinFactory factory;

		public Trail(AssassinFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bulletTrail);
			this.factory = factory;
			light = factory.bulletTrailLight;
			setCurrentAnimation(new GameAnimation(factory.bulletExplodeAnimation, startTime));
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.bulletExplodeAnimation;
		}

	}
}
