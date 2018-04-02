package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

class FireSlimeBullet extends Projectile {

	private final FireSlimeFactory factory;

	public FireSlimeBullet(FireSlimeFactory factory, Vector2 origin, float startTime) {
		super(new Body(origin, new Vector2(6, 6)), startTime, factory.bullet);
		this.factory = factory;
		light = factory.bulletLight;
		setCurrentAnimation(new GameAnimation(getAnimation(getSelfMovement()), startTime));
	}

	@Override
	protected Animation<TextureRegion> getAnimation(Vector2 direction) {
		return factory.projectileAnimation;
	}

	@Override
	protected Particle createExplosion(GameState state, Vector2 origin) {
		return new Explosion(factory, getPos(), state.getStateTime());
	}

	class Explosion extends Particle {

		private final FireSlimeFactory factory;

		public Explosion(FireSlimeFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, new Vector2(6, 6)), startTime, factory.bulletExplosion);
			this.factory = factory;
			light = factory.bulletLight;
			setCurrentAnimation(new GameAnimation(factory.explosionAnimation, startTime));
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.explosionAnimation;
		}

	}
}
