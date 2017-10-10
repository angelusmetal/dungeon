package com.dungeon.movement;

import com.badlogic.gdx.math.Vector2;

public interface Movable {

	void setSelfXMovement(float x);
	void setSelfYMovement(float y);
	void setSelfMovement(Vector2 vector);
	Vector2 getSelfMovement();
}
