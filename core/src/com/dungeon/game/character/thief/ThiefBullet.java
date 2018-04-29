package com.dungeon.game.character.thief;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

class ThiefBullet extends Projectile {

	private static final Vector2 BOUNDING_BOX = new Vector2(6, 6);
	private static final Vector2 DRAW_OFFSET = new Vector2(4, 4);

	private final ThiefFactory factory;

	public ThiefBullet(ThiefFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bullet);
		this.factory = factory;
		light = factory.bulletLight;
		setCurrentAnimation(new GameAnimation(factory.bulletFlyAnimation, startTime));
	}

	@Override
	protected Particle createExplosion(GameState state, Vector2 origin) {
		return new Explosion(factory, getPos(), state.getStateTime());
	}

	@Override
	protected Particle createTrail(GameState state, Vector2 origin) {
		return new Trail(factory, origin, state.getStateTime());
	}

	class Explosion extends Particle {

		public Explosion(ThiefFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bulletExplosion);
			light = factory.bulletLight;
			setCurrentAnimation(new GameAnimation(factory.bulletExplodeAnimation, startTime));
		}

	}

	public static class Trail extends Particle {

		public Trail(ThiefFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.bulletTrail);
			light = factory.bulletTrailLight;
			setCurrentAnimation(new GameAnimation(factory.bulletExplodeAnimation, startTime));
		}

	}
}
