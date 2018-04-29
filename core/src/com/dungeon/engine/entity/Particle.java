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

	/** Fade out particle */
	static public MutatorSupplier<Particle> fadeOut(float alpha) {
		return (p) -> {
			// Fade until the end of life
			return (particle, state) -> particle.color.a = (1 - (state.getStateTime() - particle.getStartTime()) / particle.getTimeToLive()) * alpha;
		};
	}

	/** Oscillate horizontally */
	static public MutatorSupplier<Particle> hOscillate(float frequency, float amplitude) {
		return (p) -> {
			// Randomize phase so each particle oscillates differently
			float phase = Rand.nextFloat(6.28f);
			return (particle, state) -> particle.impulse((float) Math.sin((state.getStateTime() + phase) * frequency) * amplitude, 0);
		};
	}

	/** Accelerate/decelerate particle in its current direction */
	static public MutatorSupplier<Particle> accel(float acceleration) {
		return (p) -> (particle, state) -> particle.speed += acceleration * state.getStateTime();
	}

	/** Accelerate/decelerate particle vertically */
	static public MutatorSupplier<Particle> zAccel(float acceleration) {
		return (p) -> (particle, state) -> particle.zSpeed += acceleration * state.getFrameTime();
	}

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
	/** Particle color */
	protected final Color color;
	/** Mutator */
	protected final List<Mutator<Particle>> mutator;

	public static class Builder {
		protected float speed = 1;
		protected float zSpeed = 0;
		protected int bounciness = 0;
		protected float autoseek = 0;
		protected float targetRadius = 0;
		protected Function<Entity, Boolean> targetPredicate = (entity) -> false;
		protected float timeToLive;
		protected Color color = Color.WHITE;
		protected List<MutatorSupplier<Particle>> mutators = new ArrayList<>();

		public Builder speed(float speed) {
			this.speed = speed;
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
		this.zSpeed = builder.zSpeed;
		this.bounciness = builder.bounciness;
		this.autoseek = builder.autoseek;
		this.targetRadius = builder.targetRadius * builder.targetRadius; // square is actually stored for speed
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

	@Override
	protected void onTileCollision(GameState state, boolean horizontal) {
		if (!hasExpired) {
			if (bounciness > 0) {
				bounciness--;
				setSelfImpulse(getSelfImpulse().scl(horizontal ? HORIZONTAL_BOUNCE : VERTICAL_BOUNCE));
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
		// Apply autoseek
		if (autoseek > 0) {
			applyAutoseek(state);
		} else {
			setSelfImpulse(getSelfImpulse().setLength(speed));
		}

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

	private static final Vector2 target = new Vector2();
	private static final Vector2 seek = new Vector2();

	private void applyAutoseek(GameState state) {
		boolean found = false;
		// Find closest target within range
		for (Entity entity : state.getEntities()) {
			if (targetPredicate.apply(entity)) {
				target.set(entity.getPos()).sub(getPos());
				float len = target.len2();
				if (len < targetRadius && (!found || len < seek.len2())) {
					found = true;
					seek.set(target);
				}
			}
		}
		// If a target has been found, autoseek
		if (found) {
			float seekClamp = autoseek * speed;
			float speedClamp = speed - seekClamp;
			seek.clamp(seekClamp, seekClamp);
			setSelfImpulse(getSelfImpulse().setLength(speedClamp).add(seek));
		}
	}

}
