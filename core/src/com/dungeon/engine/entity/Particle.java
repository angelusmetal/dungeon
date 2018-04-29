package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.random.Rand;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.state.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base class for all projectiles
 */
public abstract class Particle extends Entity implements Movable, Drawable {

	static private final Vector2 VERTICAL_BOUNCE = new Vector2(1, -1);
	static private final Vector2 HORIZONTAL_BOUNCE = new Vector2(-1, 1);

//	/** Projectile bounciness; 0 means no bounce (explode), 1 means perfect elastic bounce, in-between is bounce with absorption) */
//	protected int bounciness;
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
	/** Particle color */
	protected final Color color;
	/** Mutators */
	protected final List<Mutator<Particle>> mutator;

	public static class Builder {
		protected float speed = 1;
		protected float zSpeed = 0;
		protected float friction = 0;
		protected int bounciness = 0;
		protected Function<Entity, Boolean> targetPredicate = (entity) -> false;
		protected float timeToLive;
		protected Color color = Color.WHITE;
		protected List<MutatorSupplier<Particle>> mutators = new ArrayList<>();

		public Builder speed(float speed) {
			this.speed = speed;
			return this;
		}

		public Builder friction(float friction) {
			this.friction = friction;
			return this;
		}

		public Builder bounciness(int bounciness) {
			this.bounciness = bounciness;
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

		public Builder color(Color color) {
			this.color = color;
			return this;
		}

		public Builder mutate(MutatorSupplier<Particle> mutator) {
			this.mutators.add(mutator);
			return this;
		}

	}

	public Particle(Body body, Vector2 drawOffset, float startTime, Builder builder) {
		super(body, drawOffset);
		this.startTime = startTime;
		this.speed = builder.speed;
		this.friction = builder.friction;
		this.zSpeed = builder.zSpeed;
		this.bounciness = builder.bounciness;
		this.targetPredicate = builder.targetPredicate;
		this.timeToLive = builder.timeToLive;
		this.color = builder.color.cpy();
		this.drawContext = new ColorContext(this.color);
		this.mutator = builder.mutators.stream().map(m -> m.get(this)).collect(Collectors.toList());
	}

	abstract protected Animation<TextureRegion> getAnimation(Vector2 direction);

	public float getSpeed() {
		return speed;
	}

	public float getStartTime() {
		return startTime;
	}

	public float getTimeToLive() {
		return timeToLive;
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

//	@Override
//	protected void onTileCollision(GameState state, boolean horizontal) {
//		if (!hasExpired && bounciness > 0) {
//			System.out.println("WTF!");
//			expire(state);
//		}
//	}

	@Override
	protected void onTileCollision(GameState state, boolean horizontal) {
		if (!hasExpired) {
			if (bounciness > 0) {
				bounciness--;
				setSelfImpulse(getSelfImpulse().scl(horizontal ? HORIZONTAL_BOUNCE : VERTICAL_BOUNCE));
				//getMovement().scl(horizontal ? HORIZONTAL_BOUNCE : VERTICAL_BOUNCE);
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
		// Apply vertical acceleration & bounciness
		if (!hasExpired) {
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
			setInvertX(getSelfImpulse().x < 0);
			Animation<TextureRegion> currentAnimation = getCurrentAnimation().getAnimation();
			Animation<TextureRegion> newAnimation = getAnimation(getSelfImpulse());
			if (newAnimation != currentAnimation) {
				setCurrentAnimation(new GameAnimation(newAnimation, state.getStateTime()));
			}
		}

		// Apply mutators
		mutator.forEach(m -> m.accept(this, state));
	}

}
