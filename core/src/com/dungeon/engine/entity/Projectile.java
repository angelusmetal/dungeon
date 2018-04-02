package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.state.GameState;

import java.util.function.Function;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends Particle implements Movable, Drawable {

	public static final Function<Entity, Boolean> NO_FRIENDLY_FIRE = entity -> !(entity instanceof PlayerCharacter) && entity.isSolid();

	/** Damage to inflict upon hitting a target */
	protected final int damage;

	public static class Builder extends Particle.Builder {
		private int damage;

		public Builder speed(float speed) {
			this.speed = speed;
			return this;
		}

		public Builder acceleration(float acceleration) {
			this.acceleration = acceleration;
			return this;
		}

		public Builder bounciness(int bounciness) {
			this.bounciness = bounciness;
			return this;
		}

		public Builder autoseek(float autoseek) {
			this.autoseek = autoseek;
			return this;
		}

		public Builder targetRadius(float targetRadius) {
			this.targetRadius = targetRadius;
			return this;
		}

		public Builder targetPredicate(Function<Entity, Boolean> targetPredicate) {
			this.targetPredicate = targetPredicate;
			return this;
		}

		public Builder timeToLive(float timeToLive) {
			this.timeToLive = timeToLive;
			return this;
		}

		public Builder zSpeed(float zSpeed) {
			this.zSpeed = zSpeed;
			return this;
		}

		public Builder zAcceleration(float zAcceleration) {
			this.zAcceleration = zAcceleration;
			return this;
		}

		public Builder damage(int damage) {
			this.damage = damage;
			return this;
		}
	}

	public Projectile(Body body, float startTime, Builder builder) {
		super(body, startTime, builder);
		this.damage = builder.damage;
	}

	/**
	 * Triggers the projectile explosion
	 */
	@Override
	public void expire(GameState state) {
		super.expire(state);
		state.addEntity(createExplosion(state, getPos()));
	}

	abstract protected Particle createExplosion(GameState state, Vector2 origin);

	@Override
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (!hasExpired && targetPredicate.apply(entity) && entity.canBeHit(state)) {
			expire(state);
			entity.hit(state, damage);
			return true;
		} else {
			return false;
		}
	}

}
