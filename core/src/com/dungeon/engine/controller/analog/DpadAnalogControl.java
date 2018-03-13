package com.dungeon.engine.controller.analog;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.EnumMap;

/**
 * A analog control backed by a controller POV
 */
public class DpadAnalogControl extends AnalogControl implements ControllerListener {

	private static final EnumMap<PovDirection, Vector2> POV_TO_VECTOR2 = new EnumMap<>(PovDirection.class);

	static {
		POV_TO_VECTOR2.put(PovDirection.north, new Vector2(0, 1));
		POV_TO_VECTOR2.put(PovDirection.northEast, new Vector2(1, 1));
		POV_TO_VECTOR2.put(PovDirection.east, new Vector2(1, 0));
		POV_TO_VECTOR2.put(PovDirection.southEast, new Vector2(1, -1));
		POV_TO_VECTOR2.put(PovDirection.south, new Vector2(0, -1));
		POV_TO_VECTOR2.put(PovDirection.southWest, new Vector2(-1, -1));
		POV_TO_VECTOR2.put(PovDirection.west, new Vector2(-1, 0));
		POV_TO_VECTOR2.put(PovDirection.northWest, new Vector2(-1, 1));
		POV_TO_VECTOR2.put(PovDirection.center, new Vector2(0, 0));
	}

	private final int povCode;

	/**
	 * Create a analog control governed by a controller POV
	 * @param povCode code for the POV control within the controller
	 */
	public DpadAnalogControl(int povCode) {
		this.povCode = povCode;
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
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection direction) {
		if (povCode == this.povCode) {
			notifyListeners(POV_TO_VECTOR2.get(direction));
			return true;
		}
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
