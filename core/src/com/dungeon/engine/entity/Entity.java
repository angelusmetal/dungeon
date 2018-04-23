package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

abstract public class Entity implements Drawable, Movable {

	private GameAnimation currentAnimation;
	/**
	 * Self impulse the entity will constantly applied. Gets added to the current movement vector at a rate of 1x per
	 * second.
	 */
	private final Vector2 selfImpulse = new Vector2();
	/**
	 * Movement vector
	 */
	private final Vector2 movement = new Vector2();

	private final Body body;
	/** Vertical coordinate */
	protected float z;
	protected float speed = 3;
	protected float friction = 10;
	private boolean invertX = false;

	protected boolean expired;
	protected float health = 100;
	protected int maxHealth = 100;

	protected Light light = null;
	protected DrawContext drawContext = DrawContext.NONE;

	protected Entity(Body body) {
		this.body = body;
	}

	@Override
	public TextureRegion getFrame(float stateTime) {
		return currentAnimation.getKeyFrame(stateTime);
	}

	protected GameAnimation getCurrentAnimation() {
		return currentAnimation;
	}

	protected void setCurrentAnimation(GameAnimation currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	@Override
	public boolean invertX() {
		return invertX;
	}

	protected void setInvertX(boolean invertX) {
		this.invertX = invertX;
	}

	@Override
	public Vector2 getPos() {
		return body.getOrigin();
	}

	public float getZPos() {
		return z;
	}

	protected Vector2 getMovement() {
		return movement;
	}

	@Override
 	public Vector2 getDrawOffset() {
		return currentAnimation.getDrawOffset();
	}

	@Override
	public void setSelfImpulse(Vector2 vector) {
		selfImpulse.set(vector);
		onSelfMovementUpdate();
	}

	@Override
	public void setSelfImpulse(float x, float y) {
		selfImpulse.set(x, y);
		onSelfMovementUpdate();
	}

	@Override
	public Vector2 getSelfImpulse() {
		return selfImpulse;
	}


	private static final Vector2 frameMovement = new Vector2();
	private static final Vector2 stepX = new Vector2();
	private static final Vector2 stepY = new Vector2();

	@Override
	public void move(GameState state) {

		// Update movement
		float oldLength = movement.len();
		movement.add(selfImpulse.x * speed, selfImpulse.y * speed);
		float newLength = movement.len();

		// Even though an impulse can make the movement exceed the speed, selfImpulse should not help exceed it
		// (otherwise, it would accelerate indefinetly), but it can still help decrease it
		if (newLength > oldLength && newLength > speed) {
			float diff = newLength - speed;
			frameMovement.set(selfImpulse).setLength(diff);
			movement.sub(frameMovement);
		}

		frameMovement.set(movement).scl(state.getFrameTime());

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
				collidedX = detectTileCollision(state, stepX);
			}
			if (!collidedX) {
				collidedX = detectEntityCollision(state, stepX);
			}
			if (collidedX) {
				frameMovement.x = 0;
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectTileCollision(state, stepY);
			}
			if (!collidedY) {
				collidedY = detectEntityCollision(state, stepY);
			}
			if (collidedY) {
				frameMovement.y = 0;
			}
			distance -= 1;
		}
		if (distance > 0) {
			stepX.y *= distance;
			stepY.x *= distance;
			// do remainder
			if (!collidedX) {
				body.move(stepX);
				collidedX = detectTileCollision(state, stepX);
			}
			if (!collidedX) {
				detectEntityCollision(state, stepX);
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectTileCollision(state, stepY);
			}
			if (!collidedY) {
				detectEntityCollision(state, stepY);
			}
		}

		// Decrease speed
		movement.scl(1 / (1 + (state.getFrameTime() * friction)));

		// Round out very small values
		if (Math.abs(movement.x) < 0.1f) {
			movement.x = 0;
		}
		if (Math.abs(movement.y) < 0.1f) {
			movement.y = 0;
		}
	}

	private boolean detectTileCollision(GameState state, Vector2 step) {
		int tile_size = state.getLevelTileset().tile_size;
		int left = body.getLeftTile(tile_size);
		int right = body.getRightTile(tile_size);
		int bottom = body.getBottomTile(tile_size);
		int top = body.getTopTile(tile_size);
		for (int x = left; x <= right; ++x) {
			for (int y = bottom; y <= top; ++y) {
				if (!state.getLevel().walkableTiles[x][y].isFloor() && body.intersectsTile(x, y, tile_size)) {
					body.move(step.scl(-1));
					onTileCollision(state, Math.abs(step.x) > Math.abs(step.y));
					return true;
				}
			}
		}
		return false;
	}

	protected boolean detectEntityCollision(GameState state, Vector2 step) {
		boolean pushedBack = false;
		for (Entity entity : state.getEntities()) {
			if (entity != this && collides(entity)) {
				// If this did not handle a collision with the other entity, have the other entity attempt to handle it
				if (!onEntityCollision(state, entity)) {
					entity.onEntityCollision(state, this);
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

	public void hit(GameState state, float dmg) {
		health -= dmg;
		if (health <= 0) {
			setExpired(state, true);
		}
	}

	protected Vector2 getBoundingBox() {
		return body.getBoundingBox();
	}


	@Override
	public void draw(GameState state, SpriteBatch batch, ViewPort viewPort) {
		TextureRegion characterFrame = getFrame(state.getStateTime());
		float invertX = invertX() ? -1 : 1;
		drawContext.set(batch);
		viewPort.draw(batch, characterFrame, getPos().x, getPos().y + z, invertX, getDrawOffset());
		drawContext.unset(batch);
	}

	public void drawLight(GameState state, SpriteBatch batch, ViewPort viewPort) {
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

	public void setExpired(GameState state, boolean expired) {
		this.expired = expired;
		onExpire(state);
	}

	public boolean isExpired(float time) {
		return false;
	}
	public boolean isSolid() {
		return false;
	}
	public boolean canBeHit(GameState state) {
		return isSolid();
	}
	/** Handle entity collision; true if handled; false otherwise */
	protected boolean onEntityCollision(GameState state, Entity entity) {return false;}
	protected void onExpire(GameState state) {}
	protected void onSelfMovementUpdate() {}
	protected void onTileCollision(GameState state, boolean horizontal) {}

	public void think(GameState state) {}
	public float getZIndex() {
		return 0;
	}

	public static float distance2(float distance) {
		return distance * distance;
	}

}
