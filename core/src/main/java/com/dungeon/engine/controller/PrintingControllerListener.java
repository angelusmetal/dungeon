package com.dungeon.engine.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;

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

}
