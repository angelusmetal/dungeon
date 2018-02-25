package com.dungeon.engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.GameState;

public interface Movable {

	void setSelfXMovement(float x);
	void setSelfYMovement(float y);
	void setSelfMovement(Vector2 vector);
	Vector2 getSelfMovement();
	void move(GameState gameState);
}