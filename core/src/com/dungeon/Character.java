package com.dungeon;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.movement.Movable;

public class Character implements Movable {
	private final Animation<TextureRegion> frames;
	private final Vector2 pos = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movementSpeed = new Vector2();

	public Character(Animation<TextureRegion> frames) {
		this.frames = frames;
	}

	public Vector2 getPos() {
		return pos;
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

	public Vector2 getMovementSpeed() {
		return movementSpeed;
	}

	public void setMovementSpeed(Vector2 speed) {
		this.movementSpeed.set(speed);
	}

	public void move() {
		pos.add(selfMovement);
	}

	public TextureRegion getFrame(float stateTime) {
		return frames.getKeyFrame(stateTime, true);
	}

}
