package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.game.character.assassin.AssassinFactory;
import com.dungeon.game.state.GameState;

class AcidBlob extends Particle {

	private final AcidSlimeFactory factory;

	public AcidBlob(AcidSlimeFactory factory, GameState state, Vector2 origin) {
		super(new Body(origin, new Vector2(8, 8)), state.getStateTime(), factory.blob);
		this.factory = factory;
		getPos().add(0, -8);
		z = 8;
		zSpeed = (float) Math.random() * 50 + 50;
		setSelfXMovement((float) Math.random() * 100f - 50f);
		setSelfYMovement((float) Math.random() * 20f - 10f);
		setCurrentAnimation(new GameAnimation(factory.blobAnimation, state.getStateTime()));
		light = factory.poolLight;
		drawContext = new ColorContext(new Color(1, 1, 1, 0.5f));
	}

	@Override
	public float getZIndex() {
		return -1;
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 5 * state.getFrameTime());
			return true;
		} else {
			return false;
		}
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
			super(new Body(origin, new Vector2(6, 6)), startTime, factory.splat);
			this.factory = factory;
			light = factory.poolLight;
			setCurrentAnimation(new GameAnimation(factory.splatAnimation, startTime));
		}

		@Override
		protected Animation<TextureRegion> getAnimation(Vector2 direction) {
			return factory.splatAnimation;
		}
	}
}
