package com.dungeon.game.controller;

import com.badlogic.gdx.controllers.Controller;
import com.dungeon.engine.controller.ControllerConfig;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.pov.PovToggle;
import com.dungeon.engine.controller.pov.PovTrigger;
import com.dungeon.engine.controller.toggle.ControllerToggle;
import com.dungeon.engine.controller.trigger.Trigger;

public class ControllerControlBundle extends ControlBundle {
	public ControllerControlBundle(Controller controller, ControllerConfig config) {
		// Create directional controls
		StickAnalogControl moveStick = new StickAnalogControl(config.getMoveAxisX(), config.getMoveAxisY(), config.getMoveAxisXInvert(), config.getMoveAxisYInvert());
		StickAnalogControl aimStick = new StickAnalogControl(config.getAimAxisX(), config.getAimAxisY(), config.getAimAxisXInvert(), config.getAimAxisYInvert());
		PovTrigger povTrigger = new PovTrigger(new PovToggle(moveStick));

		// Create a toggle and a trigger version for action keys
		ControllerToggle toggleA = new ControllerToggle(config.getButtonA());
		ControllerToggle toggleB = new ControllerToggle(config.getButtonB());
		ControllerToggle toggleX = new ControllerToggle(config.getButtonX());
		ControllerToggle toggleY = new ControllerToggle(config.getButtonY());
		ControllerToggle toggleL1 = new ControllerToggle(config.getButtonL1());
		ControllerToggle toggleL2 = new ControllerToggle(config.getButtonL2());
		ControllerToggle toggleR1 = new ControllerToggle(config.getButtonR1());
		ControllerToggle toggleR2 = new ControllerToggle(config.getButtonR2());
		Trigger triggerA = new Trigger(toggleA);
		Trigger triggerB = new Trigger(toggleB);
		Trigger triggerX = new Trigger(toggleX);
		Trigger triggerY = new Trigger(toggleY);
		Trigger triggerL1 = new Trigger(toggleL1);
		Trigger triggerL2 = new Trigger(toggleL2);
		Trigger triggerR1 = new Trigger(toggleR1);
		Trigger triggerR2 = new Trigger(toggleR2);

		// Wire controls to the controller
		controller.addListener(moveStick);
		controller.addListener(aimStick);
		controller.addListener(toggleA);
		controller.addListener(toggleB);
		controller.addListener(toggleX);
		controller.addListener(toggleY);
		controller.addListener(toggleL1);
		controller.addListener(toggleL2);
		controller.addListener(toggleR1);
		controller.addListener(toggleR2);

		// Wire controls to the controller methods
		moveStick.addListener(this::updateDirection);
		aimStick.addListener(this::updateAim);
		povTrigger.addListener(this::povTrigger);
		toggleA.addListener(this::toggleA);
		toggleB.addListener(this::toggleB);
		toggleX.addListener(this::toggleX);
		toggleY.addListener(this::toggleY);
		toggleL1.addListener(this::toggleL1);
		toggleL2.addListener(this::toggleL2);
		toggleR1.addListener(this::toggleR1);
		toggleR2.addListener(this::toggleR2);
		triggerA.addListener(this::triggerA);
		triggerB.addListener(this::triggerB);
		triggerX.addListener(this::triggerX);
		triggerY.addListener(this::triggerY);
		triggerL1.addListener(this::triggerL1);
		triggerL2.addListener(this::triggerL2);
		triggerR1.addListener(this::triggerR1);
		triggerR2.addListener(this::triggerR2);

//		// Debug logging
//		moveStick.addListener((vec) -> System.out.println("[" + controller.getName() + "] Vector: " + vec));
//		triggerA.addListener(() -> System.out.println("[" + controller.getName() + "] Action 1"));
//		triggerB.addListener(() -> System.out.println("[" + controller.getName() + "] Action 2"));
//		triggerX.addListener(() -> System.out.println("[" + controller.getName() + "] Action 3"));
//		triggerY.addListener(() -> System.out.println("[" + controller.getName() + "] Action 4"));
	}
}
