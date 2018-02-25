package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.GameState;

abstract public class Entity<A extends Enum<A>> implements Drawable, Movable {

	private GameAnimation<A> currentAnimation;
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movement = new Vector2();
	private final Body body;
	protected float maxSpeed = 3;
	private boolean invertX = false;

	protected boolean expired;
	protected int health = 100;
	protected int maxHealth = 100;

	protected Entity(Body body) {
		this.body = body;
	}

	@Override
	public TextureRegion getFrame(float stateTime) {
		return currentAnimation.getKeyFrame(stateTime);
	}

	protected GameAnimation<A> getCurrentAnimation() {
		return currentAnimation;
	}

	protected void setCurrentAnimation(GameAnimation<A> currentAnimation) {
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

	protected Vector2 getMovement() {
		return movement;
	}

	@Override
	public Vector2 getDrawOffset() {
		return currentAnimation.getDrawOffset();
	}

	@Override
	public void setSelfXMovement(float x) {
		selfMovement.x = x;
		onSelfMovementUpdate();
	}

	@Override
	public void setSelfYMovement(float y) {
		selfMovement.y = y;
		onSelfMovementUpdate();
	}

	@Override
	public void setSelfMovement(Vector2 vector) {
		selfMovement.set(vector);
		onSelfMovementUpdate();
	}

	@Override
	public Vector2 getSelfMovement() {
		return selfMovement;
	}

	@Override
	public void move(GameState state) {
		// Update movementSpeed
		movement.add(selfMovement);
		movement.clamp(0, maxSpeed);

		float distance = movement.len();
		// Split into 1 px steps, and decompose in axes
		Vector2 stepX = movement.cpy().clamp(1,1);
		Vector2 stepY = stepX.cpy();
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
				movement.x = 0;
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectTileCollision(state, stepY);
			}
			if (!collidedY) {
				collidedY = detectEntityCollision(state, stepY);
			}
			if (collidedY) {
				movement.y = 0;
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
		movement.scl(0.9f);
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
				if (!state.getLevel().walkableTiles[x][y] && body.intersectsTile(x, y, tile_size)) {
					body.move(step.scl(-1));
					onTileCollision();
					return true;
				}
			}
		}
		return false;
	}

	private boolean detectEntityCollision(GameState state, Vector2 step) {
		boolean pushedBack = false;
		for (Entity<?> entity : state.getEntities()) {
			if (entity != this && collides(entity.body)) {
				onEntityCollision(state, entity);
				// If collides with a solid entity, push back
				if (!pushedBack && entity.isSolid()) {
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

	public void hit(GameState state, int dmg) {
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
		batch.draw(characterFrame, (getPos().x - viewPort.xOffset - getDrawOffset().x * invertX) * viewPort.scale, (getPos().y - viewPort.yOffset - getDrawOffset().y) * viewPort.scale, characterFrame.getRegionWidth() * viewPort.scale * invertX, characterFrame.getRegionHeight() * viewPort.scale);
	}

	public void setExpired(GameState state, boolean expired) {
		this.expired = expired;
		onExpire(state);
	}

	abstract public boolean isExpired(float time);
	abstract public boolean isSolid();
	protected void onEntityCollision(GameState state, Entity<?> entity) {}
	protected void onExpire(GameState state) {}
	protected void onSelfMovementUpdate() {}
	protected void onTileCollision() {}

	public void think(GameState state) {}

}