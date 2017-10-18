package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.GameAnimation;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;
import com.dungeon.viewport.ViewPort;

abstract public class Entity<A extends Enum<A>> implements Drawable, Movable {

	private GameAnimation<A> currentAnimation;
	private final Vector2 pos = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movement = new Vector2();
	private final Vector2 hitBox = new Vector2();
	private float maxSpeed = 3;
	private boolean invertX = false;

	protected boolean expired;
	protected int health = 100;
	protected int maxHealth = 100;

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
		} else {
			prevXTile = xTile; // This is to prevent a collision bug
		}
		if (prevYTile != yTile && !state.getLevel().walkableTiles[prevXTile][yTile]) {
			pos.y -= movement.y;
		}

		// Decrease speed
		movement.scl(0.9f);
		// Zero out very small values
		if (Math.abs(movement.x) < 0.1f) {
			movement.x = 0;
		}
		if (Math.abs(movement.y) < 0.1f) {
			movement.y = 0;
		}
	}

	@Override
	public void moveTo(Vector2 pos) {
		this.pos.set(pos);
	}

	public boolean collides(Vector2 pos) {
		return  pos.x >= (getPos().x - hitBox.x / 2) &&
				pos.x <= (getPos().x + hitBox.x / 2) &&
				pos.y >= (getPos().y - hitBox.y / 2) &&
				pos.y <= (getPos().y + hitBox.y / 2);
	}

	public void hurt(int dmg) {
		health -= dmg;
		if (health < 0) {
			expired = true;
		}
	}

	abstract public boolean isExpired(float time);
	abstract public boolean isSolid();

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

}
