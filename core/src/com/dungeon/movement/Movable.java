package com.dungeon.movement;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.GameState;

public interface Movable {

	void setSelfXMovement(float x);
	void setSelfYMovement(float y);
	Vector2 getSelfMovement();
	void move(GameState gameState);
	void moveTo(Vector2 pos);
}
