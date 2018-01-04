package com.dungeon.engine.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * A POV control backed by a controller stick
 */
class StickPovControl extends PovControl implements ControllerListener {

	private static final float MIN_AXIS_THRESHOLD = 0.2f;
	private static final PovDirection[] POV_DIRECTIONS = {
			PovDirection.northWest,
			PovDirection.north,
			PovDirection.northEast,
			PovDirection.west,
			PovDirection.center,
			PovDirection.east,
			PovDirection.southWest,
			PovDirection.south,
			PovDirection.southEast};

	private final int xAxis;
	private final int yAxis;
	private final float xMultiplier;
	private final float yMultiplier;

	private int povIndex = 4;

	public StickPovControl(int xAxis, int yAxis) {
		this.xAxis = Math.abs(xAxis);
		this.yAxis = Math.abs(yAxis);
		this.xMultiplier = this.xAxis / xAxis;
		this.yMultiplier = this.yAxis / yAxis;
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
		if (axisCode == xAxis && Math.abs(value) > MIN_AXIS_THRESHOLD) {
			povIndex += 1 * xMultiplier;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (axisCode == yAxis && Math.abs(value) > MIN_AXIS_THRESHOLD) {
			povIndex += 1 * yMultiplier;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else {
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
