package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.level.Level;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public class Character implements Movable, Drawable {
	private final Animation<TextureRegion> frames;
	private final Vector2 pos = new Vector2();
	private final Vector2 drawOffset = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movementSpeed = new Vector2();

	public Character(Animation<TextureRegion> frames) {
		this.frames = frames;
		this.drawOffset.set(getFrame(0).getRegionWidth() / 2, getFrame(0).getRegionHeight() / 2);
	}

	public Character(Animation<TextureRegion> frames, Vector2 drawOffset) {
		this.frames = frames;
		this.drawOffset.set(drawOffset);
	}

	@Override
	public Vector2 getPos() {
		return pos;
	}

	@Override
	public Vector2 getDrawOffset() {
		return drawOffset;
	}

	public void setPos(Vector2 pos) {
		this.pos.set(pos);
	}

	@Override
	public void setSelfXMovement(float x) {
		selfMovement.x = x;
	}

	@Override
	public void setSelfYMovement(float y) {
		selfMovement.y = y;
	}

	public void setSelfMovement(Vector2 vector) {
		selfMovement.set(vector);
	}

	public Vector2 getSelfMovement() {
		return selfMovement;
	}

	public Vector2 getMovementSpeed() {
		return movementSpeed;
	}

	public void setMovementSpeed(Vector2 speed) {
		this.movementSpeed.set(speed);
	}

	public void move(Level level, Tileset tileset) {
		pos.add(selfMovement);
		// Collision detection!
		int xTile = (int)pos.x / tileset.tile_width;
		int yTile = (int)pos.y / tileset.tile_height;
		if (!level.walkableTiles[xTile][yTile]) {
			pos.sub(selfMovement);
		}
	}

	public TextureRegion getFrame(float stateTime) {
		return frames.getKeyFrame(stateTime, true);
	}

}
