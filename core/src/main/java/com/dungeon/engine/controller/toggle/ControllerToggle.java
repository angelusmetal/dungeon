package com.dungeon.engine.controller.toggle;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ControllerToggle extends Toggle implements ControllerListener {

	private final int buttonCode;

	public ControllerToggle(int buttonCode) {
		this.buttonCode = buttonCode;
	}

	@Override
	public void connected(Controller controller) {

	}

	@Override
	public void disconnected(Controller controller) {

	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if (buttonCode == this.buttonCode) {
			notifyListeners(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		if (buttonCode == this.buttonCode) {
			notifyListeners(false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return false;
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
