package com.dungeon.game.character.assassin;

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

class AssassinBullet extends Projectile {

	private static final Vector2 BOUNDING_BOX = new Vector2(6, 6);
	private final AssassinFactory factory;

	public AssassinBullet(AssassinFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin,BOUNDING_BOX), startTime, factory.bullet);
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

		private final AssassinFactory factory;
		private final Color color;

		public Trail(AssassinFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), startTime, factory.bulletTrail);
			this.factory = factory;
			light = factory.bulletTrailLight;
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
