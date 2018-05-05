package com.dungeon.engine.movement;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.state.GameState;

public interface Movable {

	void setSelfImpulse(Vector2 vector);
	void setSelfImpulse(float x, float y);
	Vector2 getSelfImpulse();
	void move();
}
