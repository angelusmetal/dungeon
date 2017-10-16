package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.level.Level;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public class Projectile implements Movable, Drawable {
	private final Animation<TextureRegion> animation;
	private float timeToLive;
	private float startTime;
	private final Vector2 pos = new Vector2();
	private final Vector2 drawOffset = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movementSpeed = new Vector2();

	public Projectile(Animation<TextureRegion> animation, float timeToLive, float startTime) {
		this.animation = animation;
		this.timeToLive = timeToLive;
		this.startTime = startTime;
	}

	public boolean isDone(float time) {
		return (startTime + timeToLive) < time;
	}

	@Override
	public TextureRegion getFrame(float stateTime) {
		return animation.getKeyFrame(stateTime);
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
	@Override
	public boolean invertX() {
		return getSelfMovement().x < 0;
	}

	@Override
	public Vector2 getPos() {
		return pos;
	}

	@Override
	public Vector2 getDrawOffset() {
		return drawOffset;
	}

	@Override
	public void setSelfXMovement(float x) {
		selfMovement.x = x;
	}

	@Override
	public void setSelfYMovement(float y) {
		selfMovement.y = y;
	}

	@Override
	public void setSelfMovement(Vector2 vector) {
		selfMovement.set(vector);
	}

	@Override
	public Vector2 getSelfMovement() {
		return selfMovement;
	}

	public void setPos(Vector2 pos) {
		this.pos.set(pos);
	}
}
