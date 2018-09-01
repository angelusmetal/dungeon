package com.dungeon.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.dungeon.engine.controller.analog.KeyboardAnalogControl;
import com.dungeon.engine.controller.pov.PovToggle;
import com.dungeon.engine.controller.pov.PovTrigger;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;

public class KeyboardPlayerControlBundle extends PlayerControlBundle {
	public KeyboardPlayerControlBundle(InputMultiplexer inputMultiplexer) {
		super();

		// Create directional controls
		KeyboardAnalogControl directionalControl = new KeyboardAnalogControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
		PovTrigger povTrigger = new PovTrigger(new PovToggle(directionalControl));

		// Create a toggle and a trigger version for action keys
		KeyboardToggle action1Toggle = new KeyboardToggle(Input.Keys.SPACE);
		KeyboardToggle action2Toggle = new KeyboardToggle(Input.Keys.Z);
		KeyboardToggle action3Toggle = new KeyboardToggle(Input.Keys.X);
		KeyboardToggle action4Toggle = new KeyboardToggle(Input.Keys.C);
		Trigger action1Trigger = new Trigger(action1Toggle);
		Trigger action2Trigger = new Trigger(action2Toggle);
		Trigger action3Trigger = new Trigger(action3Toggle);
		Trigger action4Trigger = new Trigger(action4Toggle);

		// Wire controls to the keyboard
		inputMultiplexer.addProcessor(directionalControl);
		inputMultiplexer.addProcessor(action1Toggle);
		inputMultiplexer.addProcessor(action2Toggle);
		inputMultiplexer.addProcessor(action3Toggle);
		inputMultiplexer.addProcessor(action4Toggle);

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
//		directionalControl.addListener((vec) -> System.out.println("[Keyboard] Vector: " + vec));
//		action1Trigger.addListener(() -> System.out.println("[Keyboard] Action 1"));
//		action2Trigger.addListener(() -> System.out.println("[Keyboard] Action 2"));
//		action3Trigger.addListener(() -> System.out.println("[Keyboard] Action 3"));
//		action4Trigger.addListener(() -> System.out.println("[Keyboard] Action 4"));

	}
}
