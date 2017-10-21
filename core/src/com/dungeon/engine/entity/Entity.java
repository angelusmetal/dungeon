package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.GameState;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Tileset;
import com.dungeon.engine.viewport.ViewPort;

abstract public class Entity<A extends Enum<A>> implements Drawable, Movable {

	private GameAnimation<A> currentAnimation;
	private final Vector2 pos = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movement = new Vector2();
	private final Vector2 hitBox = new Vector2();
	protected float maxSpeed = 3;
	private boolean invertX = false;

	protected boolean expired;
	protected int health = 100;
	protected int maxHealth = 100;

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
		return pos;
	}

	@Override
	public void moveTo(Vector2 pos) {
		this.pos.set(pos);
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

		detectTileCollision(state);
		detectEntityCollision(state);

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

	private void detectTileCollision(GameState state) {
		Tileset tileset = state.getLevelTileset();

		// Apply movement and detect collision
		int prevXTile = (int)pos.x / tileset.tile_width;
		int prevYTile = (int)pos.y / tileset.tile_height;
		pos.add(movement);
		int xTile = (int)pos.x / tileset.tile_width;
		int yTile = (int)pos.y / tileset.tile_height;
		boolean collided = false;
		if (prevXTile != xTile && !state.getLevel().walkableTiles[xTile][prevYTile]) {
			pos.x -= movement.x;
			collided = true;
		} else {
			prevXTile = xTile; // This is to prevent a collision bug
		}
		if (prevYTile != yTile && !state.getLevel().walkableTiles[prevXTile][yTile]) {
			pos.y -= movement.y;
			collided = true;
		}
		if (collided) {
			onTileCollision();
		}
	}

	private void detectEntityCollision(GameState state) {
		for (Entity<?> entity : state.getEntities()) {
			if (collides(entity.getPos(), entity.getHitBox())) {
				onEntityCollision(state, entity);
			}
		}

	}

	public boolean collides(Vector2 pos) {
		return  pos.x >= (this.pos.x - this.hitBox.x / 2) &&
				pos.x <= (this.pos.x + this.hitBox.x / 2) &&
				pos.y >= (this.pos.y - this.hitBox.y / 2) &&
				pos.y <= (this.pos.y + this.hitBox.y / 2);
	}

	public boolean collides(Vector2 pos, Vector2 hitBox) {
		return  pos.x - hitBox.x / 2 >= (this.pos.x - this.hitBox.x / 2) &&
				pos.x + hitBox.x / 2 <= (this.pos.x + this.hitBox.x / 2) &&
				pos.y - hitBox.y / 2 >= (this.pos.y - this.hitBox.y / 2) &&
				pos.y + hitBox.x / 2 <= (this.pos.y + this.hitBox.y / 2);
	}

	public void hit(GameState state, int dmg) {
		health -= dmg;
		if (health < 0) {
			setExpired(state, true);
		}
	}

	protected void setHitBox(float x, float y) {
		this.hitBox.x = x;
		this.hitBox.y = y;
	}

	protected Vector2 getHitBox() {
		return hitBox;
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
