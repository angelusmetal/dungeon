package com.dungeon.engine.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * A directional control backed by a controller stick. It composes 2 separate axis into a Vector2 direction and
 * determines the closes POV value.
 */
public class AnalogDirectionalControl extends DirectionalControl implements ControllerListener {

	private static final float DEFAULT_MIN_AXIS_THRESHOLD = 0.2f;
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

	private final int xAxis;
	private final int yAxis;
	private final int xMultiplier;
	private final int yMultiplier;
	private final Vector2 direction;
	private final float minThreshold;

	private int povXIndex = 1;
	private int povYIndex = 1;

	/**
	 * Create an analog directional control backed by 2 analog controller axes, with a default minimum threshold of 0.2
	 * as dead zone.
	 *
	 * @param xAxis index of the X axis of the controller. If negative, the axis will be inverted.
	 * @param yAxis index of the Y axis of the controller. If negative, the axis will be inverted.
	 */
	public AnalogDirectionalControl(int xAxis, int yAxis) {
		this(xAxis, yAxis, DEFAULT_MIN_AXIS_THRESHOLD);
	}

	/**
	 * Create an analog directional control backed by 2 analog controller axes.
	 *
	 * @param xAxis        index of the X axis of the controller. If negative, the axis will be inverted.
	 * @param yAxis        index of the Y axis of the controller. If negative, the axis will be inverted.
	 * @param minThreshold indicates the minimum axis threshold; any value below that will be truncated to zero (center
	 *                     dead zone)
	 */
	public AnalogDirectionalControl(int xAxis, int yAxis, float minThreshold) {
		this.xAxis = Math.abs(xAxis);
		this.yAxis = Math.abs(yAxis);
		this.xMultiplier = this.xAxis / xAxis;
		this.yMultiplier = this.yAxis / yAxis;
		this.direction = new Vector2();
		this.minThreshold = minThreshold;
	}

	@Override
	public void connected(Controller controller) {

	}

	@Override
	public void disconnected(Controller controller) {

	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		int sign = value > 0 ? 1 : -1;
		if (axisCode == xAxis) {
			// Process the X axis
			float oldValue = direction.x;
			if (Math.abs(value) > DEFAULT_MIN_AXIS_THRESHOLD) {
				direction.x = value * xMultiplier;
				povXIndex = 1 + xMultiplier * sign;
			} else {
				// Values below the minimum get truncated
				direction.x = 0;
				povXIndex = 1;
			}
			if (direction.x != oldValue) {
				// Listeners only get notified if the value (after truncation) changes
				updateListeners(POV_DIRECTIONS[povYIndex * 3 + povXIndex], direction);
			}
		} else if (axisCode == yAxis) {
			// Process the Y axis
			float oldValue = direction.y;
			if (Math.abs(value) > DEFAULT_MIN_AXIS_THRESHOLD) {
				direction.y = value * yMultiplier;
				povYIndex = 1 + yMultiplier * sign;
			} else {
				// Values below the minimum get truncated
				direction.y = 0;
				povYIndex = 1;
			}
			if (direction.y != oldValue) {
				// Listeners only get notified if the value (after truncation) changes
				updateListeners(POV_DIRECTIONS[povYIndex * 3 + povXIndex], direction);
			}
		} else {
			// Signal not processed
			return false;
		}
		return true;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}
}
