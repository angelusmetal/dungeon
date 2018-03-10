package com.dungeon.engine.controller.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.dungeon.engine.controller.character.CharacterControl;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.directional.KeyboardDirectionalControl;
import com.dungeon.engine.controller.trigger.ControllerTriggerControl;
import com.dungeon.engine.controller.trigger.KeyboardTriggerControl;
import com.dungeon.game.GameState;

public class KeyboardPlayerControl extends PlayerControl {
	public KeyboardPlayerControl(GameState state, CharacterControl characterControl, InputMultiplexer inputMultiplexer) {
		super(state, characterControl);

		// Create controls
		KeyboardTriggerControl action1Trigger = new KeyboardTriggerControl(Input.Keys.SPACE);
		KeyboardTriggerControl action2Trigger = new KeyboardTriggerControl(Input.Keys.Z);
		KeyboardTriggerControl action3Trigger = new KeyboardTriggerControl(Input.Keys.X);
		KeyboardTriggerControl action4Trigger = new KeyboardTriggerControl(Input.Keys.C);
		KeyboardTriggerControl startTrigger = new KeyboardTriggerControl(Input.Keys.ENTER);
		KeyboardDirectionalControl directionalControl = new KeyboardDirectionalControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);

		// Wire controls to the keyboard
		inputMultiplexer.addProcessor(action1Trigger);
		inputMultiplexer.addProcessor(action2Trigger);
		inputMultiplexer.addProcessor(action3Trigger);
		inputMultiplexer.addProcessor(action4Trigger);
		inputMultiplexer.addProcessor(startTrigger);
		inputMultiplexer.addProcessor(directionalControl);

		// Wire controls to the controller methods
		directionalControl.addListener(this::updateDirection);
		action1Trigger.addListener(this::trigger1);
		action2Trigger.addListener(this::trigger2);
		action3Trigger.addListener(this::trigger3);
		action4Trigger.addListener(this::trigger4);
		startTrigger.addListener(this::start);

		// Debug logging
		directionalControl.addListener((pov, vec) -> System.out.println("[Keyboard] POV: " + pov + "; Vector: " + vec));
		action1Trigger.addListener(() -> System.out.println("[Keyboard] Action 1"));
		action2Trigger.addListener(() -> System.out.println("[Keyboard] Action 2"));
		action3Trigger.addListener(() -> System.out.println("[Keyboard] Action 3"));
		action4Trigger.addListener(() -> System.out.println("[Keyboard] Action 4"));
		startTrigger.addListener(() -> System.out.println("[Keyboard] Start"));

	}
}
