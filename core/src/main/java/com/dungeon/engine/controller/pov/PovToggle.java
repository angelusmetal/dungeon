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

	private static final PovDirection[] POV_DIRECTIONS_4 = {
			PovDirection.west,
			PovDirection.south,
			PovDirection.east,
			PovDirection.west,
			PovDirection.center,
			PovDirection.east,
			PovDirection.west,
			PovDirection.north,
			PovDirection.east
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
		updateListeners(vec2ToPov8(vector, minThreshold));
	}

	/**
	 * Translates an input vector into a 8-direction POV (plus center)
	 * @param vector input Vector2
	 * @param minThreshold minimum axis length in order not to consider it center
	 * @return converted POV
	 */
	public static PovDirection vec2ToPov8(Vector2 vector, float minThreshold) {
		if (vector.len2() < minThreshold * minThreshold) {
			return PovDirection.center;
		} else {
			int povXIndex = vector.x < 0 ? 0 : vector.x == 0 ? 1 : 2;
			int povYIndex = vector.y < 0 ? 0 : vector.y == 0 ? 1 : 2;
			return POV_DIRECTIONS[povYIndex * 3 + povXIndex];
		}
	}

	/**
	 * Translates an input vector into a 4-direction POV (north, south, east, west, plus center)
	 * @param vector input Vector2
	 * @param minThreshold minimum vector length in order not to consider it center
	 * @return converted POV
	 */
	public static PovDirection vec2ToPov4(Vector2 vector, float minThreshold) {
		if (vector.len2() < minThreshold * minThreshold) {
			return PovDirection.center;
		} else {
			int povXIndex = vector.x < 0 ? 0 : vector.x == 0 ? 1 : 2;
			int povYIndex = vector.y < 0 ? 0 : vector.y == 0 ? 1 : 2;
			return POV_DIRECTIONS_4[povYIndex * 3 + povXIndex];
		}
	}
}
