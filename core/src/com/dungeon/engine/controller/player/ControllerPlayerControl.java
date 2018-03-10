package com.dungeon.engine.controller.player;

import com.badlogic.gdx.controllers.Controller;
import com.dungeon.engine.controller.character.CharacterControl;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.trigger.ControllerTriggerControl;
import com.dungeon.game.GameState;

public class ControllerPlayerControl extends PlayerControl {
	public ControllerPlayerControl(GameState state, CharacterControl characterControl, Controller controller) {
		super(state, characterControl);

		// Create controls
		ControllerTriggerControl action1Trigger = new ControllerTriggerControl(0);
		ControllerTriggerControl action2Trigger = new ControllerTriggerControl(1);
		ControllerTriggerControl action3Trigger = new ControllerTriggerControl(2);
		ControllerTriggerControl action4Trigger = new ControllerTriggerControl(3);
		ControllerTriggerControl startTrigger = new ControllerTriggerControl(9);
		AnalogDirectionalControl directionalControl = new AnalogDirectionalControl(3, -2);

		// Wire controls to the controller
		controller.addListener(action1Trigger);
		controller.addListener(action2Trigger);
		controller.addListener(action3Trigger);
		controller.addListener(action4Trigger);
		controller.addListener(startTrigger);
		controller.addListener(directionalControl);

		// Wire controls to the controller methods
		directionalControl.addListener(this::updateDirection);
		action1Trigger.addListener(this::trigger1);
		action2Trigger.addListener(this::trigger2);
		action3Trigger.addListener(this::trigger3);
		action4Trigger.addListener(this::trigger4);
		startTrigger.addListener(this::start);

		// Debug logging
		directionalControl.addListener((pov, vec) -> System.out.println("[" + controller.getName() + "] POV: " + pov + "; Vector: " + vec));
		action1Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 1"));
		action2Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 2"));
		action3Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 3"));
		action4Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 4"));
		startTrigger.addListener(() -> System.out.println("[" + controller.getName() + "] Start"));
	}
}
