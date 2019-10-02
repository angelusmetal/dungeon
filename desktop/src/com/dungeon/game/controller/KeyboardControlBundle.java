package com.dungeon.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.dungeon.engine.controller.analog.KeyboardAnalogControl;
import com.dungeon.engine.controller.pov.PovToggle;
import com.dungeon.engine.controller.pov.PovTrigger;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;

public class KeyboardControlBundle extends ControlBundle {
	public KeyboardControlBundle(InputMultiplexer inputMultiplexer) {
		super();

		// Create directional controls
		KeyboardAnalogControl moveStick = new KeyboardAnalogControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
		KeyboardAnalogControl aimStick = new KeyboardAnalogControl(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D);
		PovTrigger povTrigger = new PovTrigger(new PovToggle(moveStick));

		// Create a toggle and a trigger version for action keys
		KeyboardToggle toggleA = new KeyboardToggle(Input.Keys.SPACE);
		KeyboardToggle toggleB = new KeyboardToggle(Input.Keys.Z);
		KeyboardToggle toggleX = new KeyboardToggle(Input.Keys.X);
		KeyboardToggle toggleY = new KeyboardToggle(Input.Keys.C);
		KeyboardToggle toggleL1 = new KeyboardToggle(Input.Keys.SHIFT_LEFT);
		KeyboardToggle toggleL2 = new KeyboardToggle(Input.Keys.CONTROL_LEFT);
		KeyboardToggle toggleR1 = new KeyboardToggle(Input.Keys.SHIFT_RIGHT);
		KeyboardToggle toggleR2 = new KeyboardToggle(Input.Keys.CONTROL_RIGHT);
		Trigger triggerA = new Trigger(toggleA);
		Trigger triggerB = new Trigger(toggleB);
		Trigger triggerX = new Trigger(toggleX);
		Trigger triggerY = new Trigger(toggleY);
		Trigger triggerL1 = new Trigger(toggleL1);
		Trigger triggerL2 = new Trigger(toggleL2);
		Trigger triggerR1 = new Trigger(toggleR1);
		Trigger triggerR2 = new Trigger(toggleR2);

		// Wire controls to the keyboard
		inputMultiplexer.addProcessor(moveStick);
		inputMultiplexer.addProcessor(aimStick);
		inputMultiplexer.addProcessor(toggleA);
		inputMultiplexer.addProcessor(toggleB);
		inputMultiplexer.addProcessor(toggleX);
		inputMultiplexer.addProcessor(toggleY);
		inputMultiplexer.addProcessor(toggleL1);
		inputMultiplexer.addProcessor(toggleL2);
		inputMultiplexer.addProcessor(toggleR1);
		inputMultiplexer.addProcessor(toggleR2);

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
