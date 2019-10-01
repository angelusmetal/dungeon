package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.render.ShadowType;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Entity implements Drawable, Movable {

	// TODO Clean this up (sort methods/attributes)

	private static final AtomicInteger sequencer = new AtomicInteger();

	private final int uniqueid = sequencer.getAndIncrement();

	private Animation<TextureRegion> animation;
	private float animationStart;
	private boolean offsetAnimation;
	/**
	 * Self impulse the entity will constantly applied. Gets added to the current movement vector at a rate of 1x per
	 * second.
	 */
	private final Vector2 selfImpulse = new Vector2();
	private final boolean canSelfImpulse = true;
	/** Movement vector */
	private final Vector2 movement = new Vector2();
	/** Physics body */
	private final Body body;
	/** Vertical coordinate */
	protected float z;
	/**
	 * Max allowed self movement (per second). It cannot self accelerate over this, but other forces can make this
	 * entity beyond this.
	 */
	protected float speed;
	/**
	 * Reduces speed
	 */
	protected float friction;
	/**
	 * How susceptible this entity is to knockback
	 */
	protected float knockback;
	/**
	 * Bounciness ratio (0...1)
	 */
	protected float bounciness;

	protected boolean expired;
	protected float health;
	protected int maxHealth;

	protected Light light;
	protected Light flare;
	protected DrawContext drawContext;
	private final Vector2 drawOffset;
	protected Vector2 drawScale = new Vector2(1, 1);
	private float rotation = 0;

	/** Aspects */
	protected List<Trait<Entity>> traits;
	protected List<Trait<Entity>> onHitTraits;
	protected List<Trait<Entity>> onExpireTraits;
	protected List<Trait<Entity>> onRestTraits;
	protected List<Trait<Entity>> onSignalTraits;

	/** Determines whether an entity can be hit */
	protected final Predicate<Entity> hitPredicate;
	/** Time upon which this projectile was spawned */
	protected final float startTime;
	/** Expiration time of entity */
	protected float expirationTime;
	/** Vertical speed */
	protected float zSpeed;
	/** Particle color */
	protected final Color color;
	/** zIndex for ordering sprites */
	private float zIndex;

	protected boolean canBlock;
	protected boolean canBeBlockedByEntities;
	protected boolean canBeBlockedByTiles;
	protected boolean canBeHit;
	protected boolean canBeHurt;
	protected boolean isStatic;
	protected ShadowType shadowType;

	/**
	 * Create an entity at origin, from the specified prototype
	 * @param prototype Prototype to build entity from
	 * @param origin Origin to build entity at
	 */
	public Entity(EntityPrototype prototype, Vector2 origin) {
		this.offsetAnimation = prototype.offsetAnimation;
		this.startAnimation(prototype.animation.get());
		if (prototype.boundingBoxOffset.len2() == 0) {
			this.body = Body.centered(origin, prototype.boundingBox);
		} else {
			this.body = Body.withOffset(origin, prototype.boundingBox, prototype.boundingBoxOffset);
		}
		this.drawOffset = prototype.drawOffset;
		this.startTime = Engine.time();
		this.speed = prototype.speed.get();
		this.zSpeed = prototype.zSpeed.get();
		this.knockback = prototype.friction.get();
		this.friction = prototype.friction.get();
		this.bounciness = prototype.bounciness;
		this.hitPredicate = prototype.hitPredicate;
		this.color = prototype.color.get();
		this.light = prototype.light != null ? new Light(prototype.light) : null; // TODO Check this null...
		this.flare = prototype.flare != null ? new Light(prototype.flare) : null; // TODO Check this null...
		this.drawContext = new ColorContext(this.color);
		this.zIndex = prototype.zIndex;
		this.z = prototype.z;
		Float timeToLive = prototype.timeToLive.get();
		this.expirationTime = timeToLive == null ? Float.MAX_VALUE : Engine.time() + timeToLive;
		this.traits = prototype.traits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		if (timeToLive != null) {
			traits.add(Traits.expireByTime().get(this));
		}
		this.onHitTraits = prototype.onHitTraits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		this.onExpireTraits = prototype.onExpireTraits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		this.onRestTraits = prototype.onRestTraits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		this.onSignalTraits = prototype.onSignalTraits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		this.maxHealth = prototype.health.get();
		this.health = maxHealth;
		this.canBlock = prototype.canBlock;
		this.canBeBlockedByEntities = prototype.canBeBlockedByEntities;
		this.canBeBlockedByTiles = prototype.canBeBlockedByTiles;
		this.canBeHit = prototype.canBeHit;
		this.canBeHurt = prototype.canBeHurt;
		this.shadowType = prototype.shadowType;
		this.isStatic = prototype.isStatic;
	}

	/**
	 * Copy constructor. Creates a copy of the provided entity at the same origin
	 * @param other Original entity to copy from
	 */
	public Entity (Entity other) {
		this.offsetAnimation = other.offsetAnimation;
		this.startAnimation(other.animation);
		this.body = Body.centered(other.getOrigin(), other.getBoundingBox());
		this.drawOffset = other.drawOffset;
		this.startTime = Engine.time();
		this.speed = other.getSpeed();
		this.zSpeed = other.getZSpeed();
		this.knockback = other.knockback;
		this.friction = other.friction;
		this.bounciness = other.bounciness;
		this.hitPredicate = other.hitPredicate;
		this.color = other.color;
		this.light = other.light != null ? other.light.cpy(): null;
		this.flare = other.flare != null ? other.flare.cpy(): null;
		this.drawContext = other.drawContext;
		this.zIndex = other.zIndex;
		this.expirationTime = other.expirationTime;
		this.traits = other.traits;
		this.onHitTraits = new ArrayList<>();
		this.onExpireTraits = new ArrayList<>();
		this.onRestTraits = new ArrayList<>();
		this.onSignalTraits = new ArrayList<>();
		this.maxHealth = other.getMaxHealth();
		this.health = other.health;
		this.canBlock = other.canBlock;
		this.canBeBlockedByEntities = other.canBeBlockedByEntities;
		this.canBeBlockedByTiles = other.canBeBlockedByTiles;
		this.canBeHit = other.canBeHit;
		this.canBeHurt = other.canBeHurt;
		this.shadowType = other.shadowType;
		this.isStatic = other.isStatic;
	}

	@Override
	public TextureRegion getFrame() {
		return animation.getKeyFrame(Engine.time() - animationStart);
	}

	public float getStartTime() {
		return startTime;
	}

	public float getExpirationTime() {
		return expirationTime;
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	public void setAnimation(Animation<TextureRegion> currentAnimation, float animationStart) {
		this.animation = currentAnimation;
		this.animationStart = animationStart;
	}

	public void startAnimation(Animation<TextureRegion> animation) {
		this.animation = animation;
		this.animationStart = offsetAnimation ? Engine.time() - Rand.between(0f, animation.getAnimationDuration()) : Engine.time();
	}

	public void updateAnimation(Animation<TextureRegion> animation) {
		if (animation != this.animation) {
			this.animation = animation;
			this.animationStart = Engine.time();
		}
	}

	public float getAnimationStart() {
		return animationStart;
	}

	public boolean isAnimationFinished() {
		return Engine.time() >= animationStart + animation.getAnimationDuration();
	}

	@Override
	public Vector2 getOrigin() {
		return body.getOrigin();
	}

	public float getZPos() {
		return z;
	}

	public void setZPos(float z) {
		this.z = z;
	}

	public float getZSpeed() {
		return zSpeed;
	}

	public void setZSpeed(float zSpeed) {
		this.zSpeed = zSpeed;
	}

	public Vector2 getMovement() {
		return movement;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
 	public Vector2 getDrawOffset() {
		return drawOffset;
	}

	public Vector2 getDrawScale() {
		return drawScale;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Body getBody() {
		return body;
	}

	@Override
	public void setSelfImpulse(Vector2 vector) {
		selfImpulse.set(vector);
	}

	@Override
	public void setSelfImpulse(float x, float y) {
		selfImpulse.set(x, y);
	}

	@Override
	public Vector2 getSelfImpulse() {
		return selfImpulse;
	}

	public void impulse(Vector2 vector) {
		movement.add(vector);
		onMovementUpdate();
	}

	public void impulse(float x, float y) {
		movement.add(x, y);
	}

	public void stop() {
		movement.set(Vector2.Zero);
	}

	public void setColor(Color color) {
		this.color.set(color);
	}

	public Color getColor() {
		return color;
	}

	public ShadowType shadowType() {
		return shadowType;
	}

	public float getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public List<Trait<Entity>> getTraits() {
		return traits;
	}

	public List<Trait<Entity>> getOnHitTraits() {
		return onHitTraits;
	}

	public List<Trait<Entity>> getOnExpireTraits() {
		return onExpireTraits;
	}

	public List<Trait<Entity>> getOnRestTraits() {
		return onRestTraits;
	}

	public List<Trait<Entity>> getOnSignalTraits() {
		return onSignalTraits;
	}

	public float getBounciness() {
		return bounciness;
	}

	public float getFriction() {
		return friction;
	}

	public void spawn() {
		// Detect collision against other entities upon spawning
		Engine.entities.colliding(this).forEach(entity -> {
			if (entity != this && collides(entity)) {
				// If this did not handle a collision with the other entity, have the other entity attempt to handle it
				if (!onEntityCollision(entity)) {
					entity.onEntityCollision(this);
				}
			}
		});
	}

	/**
	 * Move towards destination using self impulse (to get correct speed clamping), and clearing current movement (to
	 * ignore whatever push was already in place).
	 * @param destination Destination position.
	 */
	public void moveStrictlyTowards(Vector2 destination) {
		getMovement().set(Vector2.Zero);
		setSelfImpulse(destination.x - getOrigin().x, destination.y - getOrigin().y);
		getSelfImpulse().setLength2(1);
	}

	/**
	 * Impulse towards destination, with the specified vector length
	 * @param destination Destination position.
	 * @param length2 impulse length
	 */
	public void impulseTowards(Vector2 destination, float length2) {
		impulse(destination.cpy().sub(getOrigin()).setLength2(length2));
		onMovementUpdate();
	}

	public boolean collides(Body body) {
		return this.body.intersects(body);
	}

	public boolean collides(Entity entity) {
		return this.body.intersects(entity.body);
	}

	public boolean collides(float left, float right, float bottom, float top) {
		return this.body.intersects(left, right, bottom, top);
	}

	protected Vector2 getBoundingBox() {
		return body.getBoundingBox();
	}


	@Override
	public void draw(SpriteBatch batch, ViewPort viewPort) {
		drawContext.run(batch, () -> {
			TextureRegion frame = getFrame();
			batch.draw(
					frame,
					(int) (getOrigin().x - getDrawOffset().x),
					(int) (getOrigin().y - getDrawOffset().y + getZPos()),
					getDrawOffset().x,
					getDrawOffset().y,
					frame.getRegionWidth(),
					frame.getRegionHeight(),
					getDrawScale().x,
					getDrawScale().y,
					getRotation());
		});
	}

	public Light getLight() {
		return light;
	}
	public Light getFlare() {
		return flare;
	}

	public void expire() {
		if (!expired) {
			expired = true;
			onExpireTraits.forEach(m -> m.accept(this));
			onExpire();
		}
	}

	public boolean isExpired() {
		return expired;
	}

	public boolean canBlock() {
		return canBlock;
	}
	public boolean canBeBlockedByEntities() {
		return canBeBlockedByEntities;
	}
	public boolean canBeBlockedByTiles() {
		return canBeBlockedByTiles;
	}
	public boolean canBeHit() {
		return canBeHit;
	}
	public boolean canBeHurt() {
		return canBeHurt;
	}
	public boolean isStatic() {
		return isStatic;
	}

	protected void onMovementUpdate() {}
	/** Handle entity collision; true if handled; false otherwise */
	protected boolean onEntityCollision(Entity entity) {return false;}
	protected void onHit() {}
	protected void onExpire() {}
	protected void onTileCollision(boolean horizontal) {}
	/** Rest on the floor */
	protected void onGroundRest() {}

	public final void doThink() {
		think();
		// Apply traits
		traits.forEach(m -> m.accept(this));
		// Update light
		if (light != null) {
			light.update();
		}
		if (flare != null) {
			flare.update();
		}
	}

	protected void think() {}

	public float getZIndex() {
		return zIndex;
	}

	public final void signal(Entity emitter) {
		onSignal(emitter);
		onSignalTraits.forEach(m -> m.accept(this));
	}

	protected void onSignal(Entity emitter) {}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Entity entity = (Entity) o;
		return uniqueid == entity.uniqueid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueid);
	}

}
