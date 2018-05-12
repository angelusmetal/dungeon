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
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
	protected float speed = 3;
	/**
	 * Reduces speed
	 */
	protected float friction;
	/**
	 * Bounciness ratio (0...1)
	 */
	protected float bounciness = 0;
	private boolean invertX = false;

	protected boolean expired;
	protected float health = 100;
	protected int maxHealth = 100;

	protected Light light = null;
	protected DrawContext drawContext;
	private final Vector2 drawOffset;

	/** Aspects */
	protected List<Trait<Entity>> traits;

	/** Determines whether an entity is a target */
	protected final Predicate<Entity> targetPredicate;
	/** Time upon which this projectile was spawned */
	protected final float startTime;
	/** Expiration time of entity */
	protected final float expirationTime;
	/** Vertical speed */
	protected float zSpeed;
	/** Particle color */
	protected final Color color;
	/** zIndex for ordering sprites */
	private float zIndex = 0;

	private DrawFunction drawFunction;

	public Entity(Vector2 origin, EntityPrototype builder) {
		this.setCurrentAnimation(new GameAnimation(builder.animation, GameState.time()));
		this.body = new Body(origin, builder.boundingBox);
		this.drawOffset = builder.drawOffset;
		this.drawFunction = builder.drawFunction.get();
		this.startTime = GameState.time();
		this.speed = builder.speed.get();
		this.zSpeed = builder.zSpeed.get();
		this.friction = builder.friction.get();
		this.bounciness = builder.bounciness;
		this.targetPredicate = builder.targetPredicate;
		this.color = builder.color.get();
		this.light = builder.light != null ? builder.light.cpy() : null; // TODO Check this null...
		this.drawContext = new ColorContext(this.color);
		this.zIndex = builder.zIndex;
		Float timeToLive = builder.timeToLive.get();
		this.expirationTime = timeToLive == null ? Float.MAX_VALUE : GameState.time() + timeToLive;
		this.traits = builder.traits.stream().map(m -> m.get(this)).collect(Collectors.toCollection(ArrayList::new));
		if (timeToLive != null) {
			traits.add(Traits.expireByTime().get(this));
		}
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

	public boolean isCurrentAnimation(Animation<TextureRegion> animation) {
		return currentAnimation != null && currentAnimation.getAnimation() == animation;
	}

	public void setCurrentAnimation(GameAnimation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	@Override
	public boolean invertX() {
		return invertX;
	}

	public void setInvertX(boolean invertX) {
		this.invertX = invertX;
	}

	@Override
	public Vector2 getPos() {
		return body.getOrigin();
	}

	public float getZPos() {
		return z;
	}

	public void setZPos(float z) {
		this.z = z;
	}

	public Vector2 getMovement() {
		return movement;
	}

	@Override
 	public Vector2 getDrawOffset() {
		return drawOffset;
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
	}

	public void impulse(float x, float y) {
		movement.add(x, y);
	}

	public void setColor(Color color) {
		this.color.set(color);
	}

	public Color getColor() {
		return color;
	}

	private static final Vector2 frameMovement = new Vector2();
	private static final Vector2 stepX = new Vector2();
	private static final Vector2 stepY = new Vector2();

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
				collidedX = detectTileCollision(stepX);
			}
			if (!collidedX) {
				collidedX = detectEntityCollision(stepX);
			}
			if (collidedX) {
				movement.x *= -bounciness;
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectTileCollision(stepY);
			}
			if (!collidedY) {
				collidedY = detectEntityCollision(stepY);
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
				if (bounciness > 0 && Math.abs(zSpeed) < 10) {
					z = 0;
					zSpeed *= -bounciness;
				} else {
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
	public void moveStrictlyTo(Vector2 destination) {
		getMovement().set(Vector2.Zero);
		setSelfImpulse(destination.x - getPos().x, destination.y - getPos().y);
		getSelfImpulse().setLength2(1);
	}

	private boolean detectTileCollision(Vector2 step) {
		int tile_size = GameState.getLevelTileset().tile_size;
		int left = body.getLeftTile(tile_size);
		int right = body.getRightTile(tile_size);
		int bottom = body.getBottomTile(tile_size);
		int top = body.getTopTile(tile_size);
		for (int x = left; x <= right; ++x) {
			for (int y = bottom; y <= top; ++y) {
				if (!GameState.getLevel().walkableTiles[x][y].isFloor() && body.intersectsTile(x, y, tile_size)) {
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

	public void hit(float dmg) {
		health -= dmg;
		onHit();
		if (health <= 0) {
			expire();
		}
	}

	protected Vector2 getBoundingBox() {
		return body.getBoundingBox();
	}


	@Override
	public void draw(SpriteBatch batch, ViewPort viewPort) {
		drawContext.set(batch);
		drawFunction.draw(viewPort, batch, this);
		drawContext.unset(batch);
	}

	public void drawLight(SpriteBatch batch, ViewPort viewPort) {
		if (light != null) {
			float dim = light.dimmer.get();
			batch.setColor(light.color.r, light.color.g, light.color.b, light.color.a * dim);
			viewPort.draw(batch, light.texture, getPos().x, getPos().y + z, light.diameter * dim, light.rotator.get());
			batch.setColor(1, 1, 1, 1);
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
		return false;
	}
	public boolean canBeHit() {
		return isSolid();
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
	}

	protected void think() {}

	public float getZIndex() {
		return zIndex;
	}

	public static float distance2(float distance) {
		return distance * distance;
	}

}
