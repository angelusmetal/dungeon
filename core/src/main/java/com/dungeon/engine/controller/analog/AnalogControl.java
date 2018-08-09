package com.dungeon.engine.controller.analog;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * An analog directional control; notifies registered listeners.
 */
public abstract class AnalogControl {

	private List<AnalogListener> listeners = new ArrayList<>();

	public void addListener(AnalogListener listener) {
		listeners.add(listener);
	}

	protected void notifyListeners(Vector2 vectorDirection) {
		for (AnalogListener listener : listeners) {
			listener.onUpdate(vectorDirection);
		}
	}

}
