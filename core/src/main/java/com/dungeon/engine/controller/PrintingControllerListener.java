package com.dungeon.engine.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class PrintingControllerListener implements ControllerListener {

	private static final String TAG = "Controller";

	@Override
	public void connected(Controller controller) {
		Gdx.app.log(TAG, "connected() - Controller: '" + controller.getName() + "'");
	}

	@Override
	public void disconnected(Controller controller) {
		Gdx.app.log(TAG, "disconnected() - Controller: '" + controller.getName() + "'");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		Gdx.app.log(TAG, "buttonDown() - Controller: '" + controller.getName() + "', buttonCode: " + buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		Gdx.app.log(TAG, "buttonUp() - Controller: '" + controller.getName() + "', buttonCode: " + buttonCode);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		Gdx.app.log(TAG, "axisMoved() - Controller: " + controller.getName() + ", povCode: " + axisCode + ", value: " + value);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		Gdx.app.log(TAG, "povMoved() - Controller: '" + controller.getName() + "', povCode: " + povCode + ", value: " + value);
 		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		Gdx.app.log(TAG, "xSliderMoved() - Controller: '" + controller.getName() + "', sliderCode: " + sliderCode + ", value: " + value);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		Gdx.app.log(TAG, "ySliderMoved() - Controller: '" + controller.getName() + "', sliderCode: " + sliderCode + ", value: " + value);
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		Gdx.app.log(TAG, "accelerometerMoved() - Controller: '" + controller.getName() + "', accelerometerCode: " + accelerometerCode + ", value: " + value);
		return false;
	}
}
