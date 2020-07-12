package com.dungeon.game.controller;

import com.badlogic.gdx.Input;
import com.dungeon.engine.controller.KeyboardProcessor;
import com.dungeon.engine.controller.analog.KeyboardAnalogControl;
import com.dungeon.engine.controller.pov.PovToggle;
import com.dungeon.engine.controller.pov.PovTrigger;
import com.dungeon.engine.controller.toggle.Toggle;
import com.dungeon.engine.controller.trigger.Trigger;

public class KeyboardControlBundle extends ControlBundle {
	public KeyboardControlBundle(KeyboardProcessor keyboardToggle) {
		super();

		// Create directional controls
//		KeyboardAnalogControl moveStick = new KeyboardAnalogControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
//		KeyboardAnalogControl aimStick = new KeyboardAnalogControl(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);

		// Movement control stick
		Toggle toggleUp = new Toggle();
		Toggle toggleDown = new Toggle();
		Toggle toggleLeft = new Toggle();
		Toggle toggleRight = new Toggle();
		KeyboardAnalogControl moveStick = new KeyboardAnalogControl(toggleUp, toggleDown, toggleLeft, toggleRight);

		// Aim control stick
		Toggle toggleAimUp = new Toggle();
		Toggle toggleAimDown = new Toggle();
		Toggle toggleAimLeft = new Toggle();
		Toggle toggleAimRight = new Toggle();
		KeyboardAnalogControl aimStick = new KeyboardAnalogControl(toggleAimUp, toggleAimDown, toggleAimLeft, toggleAimRight);
		PovTrigger povTrigger = new PovTrigger(new PovToggle(moveStick));

		// Action toggles
		Toggle toggleA = new Toggle();
		Toggle toggleB = new Toggle();
		Toggle toggleX = new Toggle();
		Toggle toggleY = new Toggle();
		Toggle toggleL1 = new Toggle();
		Toggle toggleL2 = new Toggle();
		Toggle toggleR1 = new Toggle();
		Toggle toggleR2 = new Toggle();

		// Action triggers
		Trigger triggerA = new Trigger(toggleA);
		Trigger triggerB = new Trigger(toggleB);
		Trigger triggerX = new Trigger(toggleX);
		Trigger triggerY = new Trigger(toggleY);
		Trigger triggerL1 = new Trigger(toggleL1);
		Trigger triggerL2 = new Trigger(toggleL2);
		Trigger triggerR1 = new Trigger(toggleR1);
		Trigger triggerR2 = new Trigger(toggleR2);

		// Keyboard mapping
		keyboardToggle.register(Input.Keys.UP, toggleUp);
		keyboardToggle.register(Input.Keys.DOWN, toggleDown);
		keyboardToggle.register(Input.Keys.LEFT, toggleLeft);
		keyboardToggle.register(Input.Keys.RIGHT, toggleRight);

		keyboardToggle.register(Input.Keys.W, toggleAimUp);
		keyboardToggle.register(Input.Keys.S, toggleAimDown);
		keyboardToggle.register(Input.Keys.A, toggleAimLeft);
		keyboardToggle.register(Input.Keys.D, toggleAimRight);

		keyboardToggle.register(Input.Keys.SPACE, toggleA);
		keyboardToggle.register(Input.Keys.Z, toggleB);
		keyboardToggle.register(Input.Keys.X, toggleX);
		keyboardToggle.register(Input.Keys.C, toggleY);
		keyboardToggle.register(Input.Keys.SHIFT_LEFT, toggleL1);
		keyboardToggle.register(Input.Keys.CONTROL_LEFT, toggleL2);
		keyboardToggle.register(Input.Keys.SHIFT_RIGHT, toggleR1);
		keyboardToggle.register(Input.Keys.CONTROL_RIGHT, toggleR2);

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
//		moveStick.addListener((vec) -> System.out.println("[Keyboard] Vector: " + vec));
//		triggerA.addListener(() -> System.out.println("[Keyboard] Action 1"));
//		triggerB.addListener(() -> System.out.println("[Keyboard] Action 2"));
//		triggerX.addListener(() -> System.out.println("[Keyboard] Action 3"));
//		triggerY.addListener(() -> System.out.println("[Keyboard] Action 4"));

	}
}
