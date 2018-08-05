package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.DrawFunction;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Entity implements Drawable, Movable {

	private GameAnimation currentAnimation;
	/**
	 * Self impulse the entity will constantly applied. Gets added to the current movement vector at a rate of 1x per
	 * second.
	 */
	private final Vector2 selfImpulse = new Vector2();
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
	private boolean invertX = false;

	protected boolean expired;
	protected float health;
	protected int maxHealth;

	protected Light light;
	protected DrawContext drawContext;
	private final Vector2 drawOffset;

	/** Aspects */
	protected List<Trait<Entity>> traits;

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

	protected boolean solid;
	protected boolean canBeHit;
	protected boolean canBeHurt;
	protected boolean castsShadow;

	private DrawFunction drawFunction;

	/**
	 * Create an entity at origin, from the specified prototype
	 * @param prototype Prototype to build entity from
	 * @param origin Origin to build entity at
	 */
	public Entity(EntityPrototype prototype, Vector2 origin) {
		this.setCurrentAnimation(prototype.animation.get());
		this.body = new Body(origin, prototype.boundingBox);
		this.drawOffset = prototype.drawOffset;
		this.drawFunction = prototype.drawFunction.get();
		this.startTime = GameState.time();
		this.speed = prototype.speed.get();
		this.zSpeed = prototype.zSpeed.get();
		this.knockback = prototype.friction.get();
		this.friction = prototype.friction.get();
		this.bounciness = prototype.bounciness;
		this.hitPredicate = prototype.hitPredicate;
		this.color = prototype.color.get();
		this.light = prototype.light != null ? prototype.light.cpy() : null; // TODO Check this null...
		this.drawContext = new ColorContext(this.color);
		this.zIndex = prototype.zIndex;
		Float timeToLive = prototype.timeToLive.get();
		this.expirationTime = timeToLive == null ? Float.MAX_VALUE : GameState.time() + timeToLive;
		this.traits = prototype.traits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		if (timeToLive != null) {
			traits.add(Traits.expireByTime().get(this));
		}
		this.maxHealth = prototype.health.get();
		this.health = maxHealth;
		this.solid = prototype.solid;
		this.canBeHit = prototype.canBeHit;
		this.canBeHurt = prototype.canBeHurt;
		this.castsShadow = prototype.castsShadow;
	}

	/**
	 * Copy constructor. Creates a copy of the provided entity at the same origin
	 * @param other Original entity to copy from
	 */
	public Entity (Entity other) {
		this.setCurrentAnimation(other.currentAnimation);
		this.body = new Body(other.getOrigin(), other.getBoundingBox());
		this.drawOffset = other.drawOffset;
		this.drawFunction = other.drawFunction;
		this.startTime = other.getStartTime();
		this.speed = other.getSpeed();
		this.zSpeed = other.getZSpeed();
		this.knockback = other.knockback;
		this.friction = other.friction;
		this.bounciness = other.bounciness;
		this.hitPredicate = other.hitPredicate;
		this.color = other.color;
		this.light = other.light.cpy();
		this.drawContext = other.drawContext;
		this.zIndex = other.zIndex;
		this.expirationTime = other.expirationTime;
		this.traits = other.traits;
		this.maxHealth = other.getMaxHealth();
		this.health = other.health;
		this.solid = other.solid;
		this.canBeHit = other.canBeHit;
		this.canBeHurt = other.canBeHurt;
		this.castsShadow = other.castsShadow;
	}

	@Override
	public TextureRegion getFrame() {
		return currentAnimation.getKeyFrame(GameState.time());
	}

	public float getStartTime() {
		return startTime;
	}

	public float getExpirationTime() {
		return expirationTime;
	}

	public GameAnimation getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(GameAnimation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public void setCurrentAnimation(Animation<TextureRegion> animation) {
		this.currentAnimation = new GameAnimation(animation);
	}

	public void updateCurrentAnimation(Animation<TextureRegion> animation) {
		if (currentAnimation == null || currentAnimation.getAnimation() != animation) {
			this.currentAnimation = new GameAnimation(animation);
		}
	}

	@Override
	public boolean invertX() {
		return invertX;
	}

	public void setInvertX(boolean invertX) {
		this.invertX = invertX;
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

	public Body getBody() {
		return body;
	}

	public void setDrawFunction(DrawFunction drawFunction) {
		this.drawFunction = drawFunction;
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

	public boolean castsShadow() {
		return castsShadow;
	}

	public float getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	private static final Vector2 frameMovement = new Vector2();
	private static final Vector2 stepX = new Vector2();
	private static final Vector2 stepY = new Vector2();

	public void spawn() {
		// Detect collision against other entities upon spawning
		for (Entity entity : GameState.getEntities()) {
			if (entity != this && collides(entity)) {
				// If this did not handle a collision with the other entity, have the other entity attempt to handle it
				if (!onEntityCollision(entity)) {
					entity.onEntityCollision(this);
				}
			}
		}
	}

	@Override
	public void move() {

		// Update movement
		float oldLength = movement.len();
		movement.add(selfImpulse.x * speed, selfImpulse.y * speed);
		float newLength = movement.len();

		// Even though an impulse can make the movement exceed the speed, selfImpulse should not help exceed it
		// (otherwise, it would accelerate indefinitely), but it can still help decrease it
		if (newLength > oldLength && newLength > speed) {
			float diff = newLength - speed;
			frameMovement.set(selfImpulse).setLength(diff);
			movement.sub(frameMovement);
		}

		frameMovement.set(movement).scl(GameState.frameTime());

		float distance = frameMovement.len();

		// Split into 1 px steps, and decompose in axes
		stepX.set(frameMovement).clamp(0,1);
		stepY.set(stepX);
		stepX.y = 0;
		stepY.x = 0;

		boolean collidedX = false;
		boolean collidedY = false;
		while (distance > 1 && !(collidedX && collidedY)) {
			// do step
			if (!collidedX) {
				body.move(stepX);
				collidedX = detectEntityCollision(stepX);
			}
			if (!collidedX) {
				collidedX = detectTileCollision(stepX);
			}
			if (collidedX) {
				movement.x *= -bounciness;
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectEntityCollision(stepY);
			}
			if (!collidedY) {
				collidedY = detectTileCollision(stepY);
			}
			if (collidedY) {
				movement.y *= -bounciness;
			}
			distance -= 1;
		}
		if (distance > 0) {
			stepX.y *= distance;
			stepY.x *= distance;
			// do remainder
			if (!collidedX) {
				body.move(stepX);
				collidedX = detectTileCollision(stepX);
			}
			if (!collidedX) {
				detectEntityCollision(stepX);
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectTileCollision(stepY);
			}
			if (!collidedY) {
				detectEntityCollision(stepY);
			}
		}

		// Decrease speed
		movement.scl(1 / (1 + (GameState.frameTime() * friction)));

		// Round out very small values
		if (Math.abs(movement.x) < 0.1f) {
			movement.x = 0;
		}
		if (Math.abs(movement.y) < 0.1f) {
			movement.y = 0;
		}

		// Handle vertical movement
		if (!expired) {
			z += zSpeed * GameState.frameTime();
			if (z < 0) {
				z = 0;
				if (bounciness > 0 && Math.abs(zSpeed) > 10) {
					zSpeed *= -bounciness;
				} else {
					zSpeed = 0;
					onGroundRest();
				}
			}
		}
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
	}

	private boolean detectTileCollision(Vector2 step) {
		int tile_size = GameState.getTileset().tile_size;
		int left = body.getLeftTile(tile_size);
		int right = body.getRightTile(tile_size);
		int bottom = body.getBottomTile(tile_size);
		int top = body.getTopTile(tile_size);
		for (int x = left; x <= right; ++x) {
			for (int y = bottom; y <= top; ++y) {
				if (!GameState.getLevel().walkableTiles[x][y].isFloor() && body.intersectsTile(x, y, tile_size)) {
					// TODO we may want to enable/disable collision & pushback against solid tiles
					body.move(step.scl(-1));
					onTileCollision(Math.abs(step.x) > Math.abs(step.y));
					return true;
				}
			}
		}
		return false;
	}

	private boolean detectEntityCollision(Vector2 step) {
		boolean pushedBack = false;
		for (Entity entity : GameState.getEntities()) {
			if (entity != this && collides(entity)) {
				// If this did not handle a collision with the other entity, have the other entity attempt to handle it
				if (!onEntityCollision(entity)) {
					entity.onEntityCollision(this);
				}
				// If collides with a solid entity, push back
				if (isSolid() && !pushedBack && entity.isSolid()) {
					body.move(step.scl(-1));
					pushedBack = true;
				}
			}
		}
		return pushedBack;
	}

	public boolean collides(Vector2 pos) {
		return body.intersects(pos);
	}

	public boolean collides(Body body) {
		return this.body.intersects(body);
	}

	public boolean collides(Entity entity) {
		return this.body.intersects(entity.body);
	}

	public void hit(Attack attack) {
		if (canBeHurt()) {
			health -= attack.getDamage();
			onHit();
			if (attack.getDamage() > 1) {
				GameState.addOverlayText(new OverlayText(getOrigin(), "" + (int) attack.getDamage(), new Color(1, 0.5f, 0.2f, 0.5f)).fadeout(1).move(0, 20));
			}
			if (health <= 0) {
				expire();
			}
			if (attack.getKnockback() > 0) {
				Vector2 knockback = getOrigin().cpy().sub(attack.getEmitter().getOrigin()).setLength(attack.getKnockback() * this.knockback);
				impulse(knockback);
				System.out.println("KNOCKBACK! " + knockback);
			}
		}
	}

	protected Vector2 getBoundingBox() {
		return body.getBoundingBox();
	}


	@Override
	public void draw(SpriteBatch batch, ViewPort viewPort) {
		drawContext.run(batch, () -> drawFunction.draw(viewPort, batch, this));
	}

	public void drawLight(SpriteBatch batch, ViewPort viewPort) {
		if (light != null) {
			batch.setColor(
					Util.clamp(light.color.r * light.dim),
					Util.clamp(light.color.g * light.dim),
					Util.clamp(light.color.b * light.dim),
					Util.clamp(light.color.a * light.dim));
			viewPort.draw(batch, light.texture, getOrigin().x, getOrigin().y + z, light.diameter * light.dim, light.angle);
//			batch.setColor(1, 1, 1, 1);
		}
	}

	public Light getLight() {
		return light;
	}

	public void expire() {
		if (!expired) {
			expired = true;
			onExpire();
		}
	}

	public boolean isExpired() {
		return expired;
	}

	public boolean isSolid() {
		return solid;
	}
	public boolean canBeHit() {
		return canBeHit;
	}
	public boolean canBeHurt() {
		return canBeHurt;
	}

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
	}

	protected void think() {}

	public float getZIndex() {
		return zIndex;
	}

}
