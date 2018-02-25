package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.render.Tileset;
import com.dungeon.game.GameState;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends Entity<Projectile.AnimationType> implements Movable, Drawable {

	// TODO implement bounce and expiration action

	/**
	 * Describes animation types for projectiles
	 */
	public enum AnimationType {
		FLY_NORTH, FLY_SOUTH, FLY_SIDE, EXPLOSION
	}

	protected AnimationProvider<AnimationType> animationProvider;
	/** Projectile speed */
	protected float speed;
	/** Projectile acceleration (or deceleration) ratio */
	protected final float acceleration;
	/** Projectile bounciness; 0 means no bounce (explode), 1 means perfect elastic bounce, in-between is bounce with absorption) */
	protected final float bounciness;
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
	/** Whether this projectile is already exploding; a lot of stuff needs to be ignored if so */
	protected boolean exploding = false;

	public static class Builder {
		private float speed = 1;
		private float acceleration = 1;
		private float bounciness = 0;
		private float autoseek = 0;
		private float targetRadius = 0;
		private Function<Entity, Boolean> targetPredicate = (entity) -> false;
		private float timeToLive;
		private Runnable onExpire;

		public Builder speed(float speed) {
			this.speed = speed;
			return this;
		}

		public Builder acceleration(float acceleration) {
			this.acceleration = acceleration;
			return this;
		}

		public Builder bounciness(float bounciness) {
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
			// Move projectile in the current direction
			getPos().add(getSelfMovement());

			// Detect collision against walls
			Tileset tileset = state.getLevelTileset();
			int xTile = (int)getPos().x / tileset.tile_size;
			int yTile = (int)getPos().y / tileset.tile_size;
			if (!state.getLevel().walkableTiles[xTile][yTile]) {
				explode(state);
			} else {
				// Detect collision against entities
				for (Entity<?> entity : state.getEntities()) {
					if (entity != this && entity.isSolid() && entity.collides(getPos())) {
						onEntityCollision(state, entity);
					}
				}

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
				setSelfMovement(getSelfMovement().cpy().clamp(speedClamp, speedClamp).add(seek));
			}
		}
	}
}
