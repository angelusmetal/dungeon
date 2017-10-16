package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.GameAnimation;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

abstract public class Entity<A extends Enum<A>> implements Drawable, Movable {

	private GameAnimation<A> currentAnimation;
	private final Vector2 pos = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movement = new Vector2();
	private float maxSpeed = 3;
	private boolean invertX = false;

	public Entity() {
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
		return pos;
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

	abstract protected void onSelfMovementUpdate();

	@Override
	public Vector2 getSelfMovement() {
		return selfMovement;
	}

	@Override
	public void move(GameState state) {
		// Update movementSpeed
		movement.add(selfMovement);
		movement.clamp(0, maxSpeed);

		// TODO Maybe the level should tell us what its tileset is?
		Tileset tileset = state.getTilesetManager().getDungeonTilesetDark();

		// Apply movement and detect collision
		int prevXTile = (int)pos.x / tileset.tile_width;
		int prevYTile = (int)pos.y / tileset.tile_height;
		pos.add(movement);
		int xTile = (int)pos.x / tileset.tile_width;
		int yTile = (int)pos.y / tileset.tile_height;
		if (prevXTile != xTile && !state.getLevel().walkableTiles[xTile][prevYTile]) {
			pos.x -= movement.x;
		}
		if (prevYTile != yTile && !state.getLevel().walkableTiles[prevXTile][yTile]) {
			pos.y -= movement.y;
		}

		// Decrease speed
		movement.scl(0.9f);
	}

	@Override
	public void moveTo(Vector2 pos) {
		this.pos.set(pos);
	}

	public boolean isExpired() {
		return false;
	}
}
