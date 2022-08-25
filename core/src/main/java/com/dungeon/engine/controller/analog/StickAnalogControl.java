package com.dungeon.engine.controller.analog;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.math.Vector2;

/**
 * A analog control backed by a controller stick. It composes 2 separate axis into a Vector2 direction and
 * determines the closes POV value.
 */
public class StickAnalogControl extends AnalogControl implements ControllerListener {

	private static final float DEFAULT_MIN_AXIS_THRESHOLD = 0.2f;

	private final int xAxis;
	private final int yAxis;
	private final int xMultiplier;
	private final int yMultiplier;
	private final Vector2 direction;
	private final float minThreshold;

	/**
	 * Create an analog analog control backed by 2 analog controller axes, with a default minimum threshold of 0.2
	 * as dead zone.
	 *
	 * @param xAxis index of the X axis of the controller. If negative, the axis will be inverted.
	 * @param yAxis index of the Y axis of the controller. If negative, the axis will be inverted.
	 */
	public StickAnalogControl(int xAxis, int yAxis, boolean invertX, boolean invertY) {
		this(xAxis, yAxis, invertX, invertY, DEFAULT_MIN_AXIS_THRESHOLD);
	}

	/**
	 * Create an analog analog control backed by 2 analog controller axes.
	 *
	 * @param xAxis        index of the X axis of the controller. If negative, the axis will be inverted.
	 * @param yAxis        index of the Y axis of the controller. If negative, the axis will be inverted.
	 * @param minThreshold indicates the minimum axis threshold; any value below that will be truncated to zero (center
	 *                     dead zone)
	 */
	public StickAnalogControl(int xAxis, int yAxis, boolean invertX, boolean invertY, float minThreshold) {
		this.xAxis = Math.abs(xAxis);
		this.yAxis = Math.abs(yAxis);
		this.xMultiplier = invertX ? -1 : 1;
		this.yMultiplier = invertY ? -1 : 1;
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
			// Values below the minimum get truncated
			direction.x = Math.abs(value) > minThreshold ? value * xMultiplier : 0;
			if (direction.x != oldValue) {
				// Listeners only get notified if the value (after truncation) changes
				notifyListeners(direction);
			}
		} else if (axisCode == yAxis) {
			// Process the Y axis
			float oldValue = direction.y;
			// Values below the minimum get truncated
			direction.y = Math.abs(value) > minThreshold ? value * yMultiplier : 0;
			if (direction.y != oldValue) {
				// Listeners only get notified if the value (after truncation) changes
				notifyListeners(direction);
			}
		} else {
			// Signal not processed
			return false;
		}
		return true;
	}

}
