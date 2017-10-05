package com.dungeon;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class PrintingControllerListener implements ControllerListener {

	@Override
	public void connected(Controller controller) {
		System.out.println("connected() - Controller: '" + controller.getName() + "'");
	}

	@Override
	public void disconnected(Controller controller) {
		System.out.println("disconnected() - Controller: '" + controller.getName() + "'");
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		System.out.println("buttonDown() - Controller: '" + controller.getName() + "', buttonCode: " + buttonCode);
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		System.out.println("buttonUp() - Controller: '" + controller.getName() + "', buttonCode: " + buttonCode);
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		System.out.println("axisMoved() - Controller: " + controller.getName() + ", povCode: " + axisCode + ", value: " + value);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		System.out.println("povMoved() - Controller: '" + controller.getName() + "', povCode: " + povCode + ", value: " + value);
 		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		System.out.println("xSliderMoved() - Controller: '" + controller.getName() + "', sliderCode: " + sliderCode + ", value: " + value);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		System.out.println("ySliderMoved() - Controller: '" + controller.getName() + "', sliderCode: " + sliderCode + ", value: " + value);
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		System.out.println("accelerometerMoved() - Controller: '" + controller.getName() + "', accelerometerCode: " + accelerometerCode + ", value: " + value);
		return false;
	}
}
