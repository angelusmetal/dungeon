package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.state.GameState;

import java.util.function.Function;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends Entity<Projectile.AnimationType> implements Movable, Drawable {

	// TODO implement expiration action
	static private final Vector2 VERTICAL_BOUNCE = new Vector2(1, -1);
	static private final Vector2 HORIZONTAL_BOUNCE = new Vector2(-1, 1);

	public static final Function<Entity, Boolean> NO_FRIENDLY_FIRE = entity -> !(entity instanceof PlayerCharacter) && entity.isSolid();

	/**
	 * Describes animation types for projectiles
	 */
	public enum AnimationType {
		FLY_NORTH, FLY_SOUTH, FLY_SIDE, EXPLOSION
	}

	protected AnimationProvider<AnimationType> animationProvider;
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
	protected float startTime; // TODO make final
	/** Time to live (in seconds) until projectile expiration */
	protected float timeToLive; // TODO make final
	/** Action to run when the projectile's timeToLive has expired (not when it impacted) */
	protected final Runnable onExpire;
	/** Damage to inflict upon hitting a target */
	protected final int damage;
	/** Whether this projectile is already exploding; a lot of stuff needs to be ignored if so */
	protected boolean exploding = false;

	public static class Builder {
		private float speed = 1;
		private float acceleration = 1;
		private int bounciness = 0;
		private float autoseek = 0;
		private float targetRadius = 0;
		private Function<Entity, Boolean> targetPredicate = (entity) -> false;
		private float timeToLive;
		private Runnable onExpire;
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

		public Builder onExpire(Runnable onExpire) {
			this.onExpire = onExpire;
			return this;
		}

		public Builder damage(int damage) {
			this.damage = damage;
			return this;
		}
	}

	public Projectile(Body body, float startTime, Builder builder) {
		super(body);
		this.startTime = startTime;
		this.speed = builder.speed;
		this.acceleration = builder.acceleration;
		this.bounciness = builder.bounciness;
		this.autoseek = builder.autoseek;
		this.targetRadius = builder.targetRadius * builder.targetRadius; // square is actually stored for speed
		this.targetPredicate = builder.targetPredicate;
		this.timeToLive = builder.timeToLive;
		this.onExpire = builder.onExpire;
		this.damage = builder.damage;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public boolean isExpired(float time) {
		return (startTime + timeToLive) < time;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public void move(GameState state) {
		// Only if not already exploding
		if (!exploding) {
			super.move(state);
		}
	}

	@Override
	protected boolean detectEntityCollision(GameState state, Vector2 step) {
		for (Entity<?> entity : state.getEntities()) {
			if (entity != this && collides(entity)) {
				// If this did not handle a collision with the other entity, have the other entity attempt to handle it
				if (!onEntityCollision(state, entity)) {
					entity.onEntityCollision(state, this);
				}
			}
		}
		return false; // Projectiles are never pushed back
	}

	@Override
	protected void onTileCollision(GameState state, boolean horizontal) {
		if (!exploding) {
			if (bounciness > 0) {
				bounciness--;
				setSelfMovement(getSelfMovement().scl(horizontal ? HORIZONTAL_BOUNCE : VERTICAL_BOUNCE));
			} else {
				explode(state);
			}
		}
	}

	@Override
	public void onSelfMovementUpdate() {
		if (!exploding) {
			// Updates current animation based on the direction vector
			AnimationType animationType;
			if (Math.abs(getSelfMovement().x) > Math.abs(getSelfMovement().y)) {
				// Sideways animation; negative values invert X
				animationType = AnimationType.FLY_SIDE;
				setInvertX(getSelfMovement().x < 0);
			} else {
				// North / south animation
				animationType = getSelfMovement().y < 0 ? AnimationType.FLY_SOUTH : AnimationType.FLY_NORTH;
			}
			setCurrentAnimation(animationProvider.get(animationType));
		}

	}

	/**
	 * Triggers the projectile explosion
	 */
	protected void explode(GameState state) {
		// Set exploding status and animation (and TTL to expire right after explosion end)
		exploding = true;
		setCurrentAnimation(animationProvider.get(AnimationType.EXPLOSION));
		// TODO should spawn an explosion object instead
		startTime = state.getStateTime();
		timeToLive = getCurrentAnimation().getDuration();
	}

	@Override
	public void think(GameState state) {
		speed *= acceleration;
		if (autoseek > 0) {
			Vector2 seek = null;
			// Find closest target within range
			for (Entity entity : state.getEntities()) {
				if (targetPredicate.apply(entity)) {
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
		} else {
			setSelfMovement(getSelfMovement().setLength(speed));
		}
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity<?> entity) {
		if (!exploding && NO_FRIENDLY_FIRE.apply(entity)) {
			explode(state);
			entity.hit(state, damage);
			return true;
		} else {
			return false;
		}
	}

}
