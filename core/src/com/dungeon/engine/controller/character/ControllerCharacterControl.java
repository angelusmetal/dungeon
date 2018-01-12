package com.dungeon.engine.controller.character;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.trigger.ControllerTriggerControl;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.GameState;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A CharacterControl implementation that uses a controller for controlling the character
 */
public class ControllerCharacterControl extends CharacterControl {

	private final Supplier<Vector2> positionProvider;
	private final Function<Vector2, PlayerCharacter> characterSupplier;

	public ControllerCharacterControl(GameState state, Controller controller, Supplier<Vector2> positionProvider, Function<Vector2, PlayerCharacter> characterSupplier) {
		super(state);
		this.positionProvider = positionProvider;
		this.characterSupplier = characterSupplier;

		// Create controls for directional movement, fire and start
		AnalogDirectionalControl directionalControl = new AnalogDirectionalControl(3, -2);
		ControllerTriggerControl fireControl = new ControllerTriggerControl(0);
		ControllerTriggerControl startTrigger = new ControllerTriggerControl(9);

		// Wire controls to the character
		directionalControl.addListener(this::updateDirection);
		fireControl.addListener(this::fire);
		startTrigger.addListener(this::start);

		// Wire controls to the keyboard
		controller.addListener(startTrigger);
		controller.addListener(directionalControl);
		controller.addListener(fireControl);

		// Debug stuff
//		directionalControl.addListener((pov, vec) -> System.out.println("[" + controller.getName() + "] POV: " + pov + "; Vector: " + vec));
//		fireControl.addListener((bool) -> System.out.println("[" + controller.getName() + "] Fire " + (bool ? "(pressed)" : "(unpressed)")));
//		startTrigger.addListener((bool) -> System.out.println("[" + controller.getName() + "] Start " + (bool ? "(pressed)" : "(unpressed)")));
	}

	@Override
	Vector2 getStartingPosition() {
		return positionProvider.get();
	}

	@Override
	PlayerCharacter getCharacter(Vector2 pos) {
		return characterSupplier.apply(pos);
	}
}
