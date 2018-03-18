package com.dungeon.engine.controller.player;

import com.badlogic.gdx.controllers.Controller;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.pov.PovToggle;
import com.dungeon.engine.controller.pov.PovTrigger;
import com.dungeon.engine.controller.toggle.ControllerToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.game.state.GameState;

public class ControllerPlayerControlBundle extends PlayerControlBundle {
	public ControllerPlayerControlBundle(GameState state, Controller controller) {
		super(state);

		// Create directional controls
		StickAnalogControl directionalControl = new StickAnalogControl(3, -2);
		PovTrigger povTrigger = new PovTrigger(new PovToggle(directionalControl));

		// Create a toggle and a trigger version for action keys
		ControllerToggle action1Toggle = new ControllerToggle(0);
		ControllerToggle action2Toggle = new ControllerToggle(1);
		ControllerToggle action3Toggle = new ControllerToggle(2);
		ControllerToggle action4Toggle = new ControllerToggle(3);
		Trigger action1Trigger = new Trigger(action1Toggle);
		Trigger action2Trigger = new Trigger(action2Toggle);
		Trigger action3Trigger = new Trigger(action3Toggle);
		Trigger action4Trigger = new Trigger(action4Toggle);

		// Wire controls to the controller
		controller.addListener(directionalControl);
		controller.addListener(action1Toggle);
		controller.addListener(action2Toggle);
		controller.addListener(action3Toggle);
		controller.addListener(action4Toggle);

		// Wire controls to the controller methods
		directionalControl.addListener(this::updateDirection);
		povTrigger.addListener(this::povTrigger);
		action1Toggle.addListener(this::toggle1);
		action2Toggle.addListener(this::toggle2);
		action3Toggle.addListener(this::toggle3);
		action4Toggle.addListener(this::toggle4);
		action1Trigger.addListener(this::trigger1);
		action2Trigger.addListener(this::trigger2);
		action3Trigger.addListener(this::trigger3);
		action4Trigger.addListener(this::trigger4);

//		// Debug logging
//		directionalControl.addListener((vec) -> System.out.println("[" + controller.getName() + "] Vector: " + vec));
//		action1Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 1"));
//		action2Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 2"));
//		action3Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 3"));
//		action4Trigger.addListener(() -> System.out.println("[" + controller.getName() + "] Action 4"));
	}
}
