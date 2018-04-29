package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.random.Rand;
import com.dungeon.game.state.GameState;

class AcidBlob extends Particle {

	private static final Vector2 BOUNDING_BOX = new Vector2(6, 6);
	private static final Vector2 DRAW_OFFSET = new Vector2(8, 8);

	private final AcidSlimeFactory factory;

	public AcidBlob(AcidSlimeFactory factory, GameState state, Vector2 origin) {
		super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, state.getStateTime(), factory.blob);
		this.factory = factory;
		getPos().add(0, -8);
		z = 8;
		zSpeed = Rand.between(50f, 100f);
		setSelfImpulse(Rand.between(-50f, 50f), Rand.between(-10f, 10f));
		setCurrentAnimation(new GameAnimation(factory.blobAnimation, state.getStateTime()));
		light = factory.blobLight;
	}

	@Override
	public float getZIndex() {
		return -1;
	}

	@Override
	public void expire(GameState state) {
		super.expire(state);
		state.addEntity(new Explosion(factory, getPos(), state.getStateTime()));
	}

	@Override
	protected Animation<TextureRegion> getAnimation(Vector2 direction) {
		return factory.blobAnimation;
	}

	public static class Explosion extends Particle {

		private final AcidSlimeFactory factory;

		public Explosion(AcidSlimeFactory factory, Vector2 origin, float startTime) {
			super(new Body(origin, BOUNDING_BOX), DRAW_OFFSET, startTime, factory.splat);
			this.factory = factory;
			light = factory.blobLight;
			setCurrentAnimation(new GameAnimation(factory.splatAnimation, startTime));
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.splatAnimation;
		}
	}
}
