package com.dungeon.engine.controller.pov;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.analog.AnalogControl;
import com.dungeon.engine.controller.analog.AnalogListener;

import java.util.ArrayList;
import java.util.List;

public class PovToggle implements AnalogListener {

	private static final PovDirection[] POV_DIRECTIONS = {
			PovDirection.southWest,
			PovDirection.south,
			PovDirection.southEast,
			PovDirection.west,
			PovDirection.center,
			PovDirection.east,
			PovDirection.northWest,
			PovDirection.north,
			PovDirection.northEast
	};

	private static final float DEFAULT_MIN_AXIS_THRESHOLD = 0.8f;

	private final float minThreshold;
	private final List<PovToggleListener> listeners = new ArrayList<>();

	public PovToggle(AnalogControl control) {
		this(control, DEFAULT_MIN_AXIS_THRESHOLD);
	}

	public PovToggle(AnalogControl control, float minThreshold) {
		control.addListener(this);
		this.minThreshold = minThreshold;
	}

	public void addListener(PovToggleListener listener) {
		listeners.add(listener);
	}

	private void updateListeners(PovDirection direction) {
		for (PovToggleListener listener : listeners) {
			listener.onUpdate(direction);
		}
	}

	@Override
	public void onUpdate(Vector2 vector) {
		float x = Math.abs(vector.x) > minThreshold ? vector.x : 0;
		float y = Math.abs(vector.y) > minThreshold ? vector.y : 0;
		int povXIndex = x < 0 ? 0 : x == 0 ? 1 : 2;
		int povYIndex = y < 0 ? 0 : y == 0 ? 1 : 2;
		updateListeners(POV_DIRECTIONS[povYIndex * 3 + povXIndex]);
	}
}