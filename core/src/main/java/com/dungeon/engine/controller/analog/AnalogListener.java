package com.dungeon.engine.controller.analog;

import com.badlogic.gdx.math.Vector2;

/**
 * Listener for analog directional control.
 */
@FunctionalInterface
public interface AnalogListener {
	void onUpdate(Vector2 vector);
}
