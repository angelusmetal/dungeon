package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
public abstract class Particle extends Entity implements Movable, Drawable {

	static private final Vector2 VERTICAL_BOUNCE = new Vector2(1, -1);
	static private final Vector2 HORIZONTAL_BOUNCE = new Vector2(-1, 1);

	/** Projectile acceleration (or deceleration) ratio */
	protected final float acceleration;
	/** Projectile bounciness; 0 means no bounce (explode), 1 means perfect elastic bounce, in-between is bounce with absorption) */
	protected int bounciness;
	/** Autoseek ratio; 0 means no autoseek; 1 means projectile will do a hard turn towards target when within range; in-between will turn slightly */
	protected final float autoseek;
	/** Radius, in units, for detecting targets */
	protected final float targetRadius;
	/** Determines whether an entity is a target */
	protected final Function<Entity, Boolean> targetPredicate;
	/** Time upon which this projectile was spawned */
	protected final float startTime;
	/** Time to live (in seconds) until projectile expiration */
	protected final float timeToLive;
	/** Whether this particle has expired; a lot of stuff needs to be ignored if so */
	protected boolean hasExpired = false;
	/** Vertical speed */
	protected float zSpeed;
	/** Vertical acceleration */
	protected final float zAcceleration;

	public static class Builder {
		protected float speed = 1;
		protected float zSpeed = 0;
		protected float acceleration = 1;
		protected float zAcceleration = 0;
		protected int bounciness = 0;
		protected float autoseek = 0;
		protected float targetRadius = 0;
		protected Function<Entity, Boolean> targetPredicate = (entity) -> false;
		protected float timeToLive;

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

	}

	public Particle(Body body, float startTime, Builder builder) {
		super(body);
		this.startTime = startTime;
		this.speed = builder.speed;
		this.zSpeed = builder.zSpeed;
		this.acceleration = builder.acceleration;
		this.zAcceleration = builder.zAcceleration;
		this.bounciness = builder.bounciness;
		this.autoseek = builder.autoseek;
		this.targetRadius = builder.targetRadius * builder.targetRadius; // square is actually stored for speed
		this.targetPredicate = builder.targetPredicate;
		this.timeToLive = builder.timeToLive;
	}

	abstract protected Animation<TextureRegion> getAnimation(Vector2 direction);

	public float getSpeed() {
		return speed;
	}

	@Override
	public boolean isExpired(float time) {
		return hasExpired || (startTime + timeToLive) < time;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public void move(GameState state) {
		// Only if not already hasExpired
		if (!hasExpired) {
			super.move(state);
		}
	}

	@Override
	protected void onTileCollision(GameState state, boolean horizontal) {
		if (!hasExpired) {
			if (bounciness > 0) {
				bounciness--;
				setSelfMovement(getSelfMovement().scl(horizontal ? HORIZONTAL_BOUNCE : VERTICAL_BOUNCE));
			} else {
				expire(state);
			}
		}
	}

	public void expire(GameState state) {
		hasExpired = true;
		onExpire(state);
	}

	@Override
	public void think(GameState state) {
		speed += acceleration * state.getFrameTime();

		// Apply autoseek
		if (autoseek > 0) {
			applyAutoseek(state);
		} else {
			setSelfMovement(getSelfMovement().setLength(speed));
		}

		// Apply vertical acceleration & bounciness
		if (!hasExpired) {
			zSpeed += zAcceleration * state.getFrameTime();
			z += zSpeed * state.getFrameTime();
			if (z < 0) {
				if (bounciness > 0) {
					bounciness--;
					z = 0;
					zSpeed *= -0.5;
				} else {
					expire(state);
				}
			}
		}

		// Update animation
		if (!hasExpired) {
			// Updates current animation based on the direction vector
			setInvertX(getSelfMovement().x < 0);
			Animation<TextureRegion> currentAnimation = getCurrentAnimation().getAnimation();
			Animation<TextureRegion> newAnimation = getAnimation(getSelfMovement());
			if (newAnimation != currentAnimation) {
				setCurrentAnimation(new GameAnimation(newAnimation, state.getStateTime()));
			}
		}
	}

	private void applyAutoseek(GameState state) {
		Vector2 seek = null;
		// Find closest target within range
		for (Entity entity : state.getEntities()) {
			if (targetPredicate.apply(entity)) {
				// TODO Optimize to use dst2?
				Vector2 v = entity.getPos().cpy().sub(getPos());
				float len = v.len2();
				if (len < targetRadius && (seek == null || len < seek.len2())) {
					seek = v;
				}
			}
		}
		// If a target has been found, autoseek
		if (seek != null) {
			float seekClamp = autoseek * speed;
			float speedClamp = speed - seekClamp;
			seek.clamp(seekClamp, seekClamp);
			setSelfMovement(getSelfMovement().setLength(speedClamp).add(seek));
		}
	}

}
