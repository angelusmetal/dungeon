package com.dungeon.engine.controller.character;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.directional.KeyboardDirectionalControl;
import com.dungeon.engine.controller.trigger.KeyboardTriggerControl;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.GameState;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A CharacterControl implementation that uses keyboard keys for controlling the character
 */
public class KeyboardCharacterControl extends CharacterControl {

	private final Supplier<Vector2> positionProvider;
	private final Function<Vector2, PlayerCharacter> characterSupplier;

	public KeyboardCharacterControl(GameState state, InputMultiplexer inputMultiplexer, Supplier<Vector2> positionProvider, Function<Vector2, PlayerCharacter> characterSupplier) {
		super(state);
		this.positionProvider = positionProvider;
		this.characterSupplier = characterSupplier;

		// Create controls for directional movement, fire and start
		KeyboardDirectionalControl directionalControl = new KeyboardDirectionalControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
		KeyboardTriggerControl fireControl = new KeyboardTriggerControl(Input.Keys.SPACE);
		KeyboardTriggerControl startTrigger = new KeyboardTriggerControl(Input.Keys.ENTER);

		// Wire controls to the character
		directionalControl.addListener(this::updateDirection);
		fireControl.addListener(this::fire);
		startTrigger.addListener(this::start);

		// Wire controls to the keyboard
		inputMultiplexer.addProcessor(startTrigger);
		inputMultiplexer.addProcessor(directionalControl);
		inputMultiplexer.addProcessor(fireControl);

		// Debug stuff
//		directionalControl.addListener((pov, vec) -> System.out.println("[Keyboard] POV: " + pov + "; Vector: " + vec));
//		fireControl.addListener((bool) -> System.out.println("[Keyboard] Fire " + (bool ? "(pressed)" : "(unpressed)")));
//		startTrigger.addListener((bool) -> System.out.println("[Keyboard] Start " + (bool ? "(pressed)" : "(unpressed)")));
	}

	@Override
	Vector2 getStartingPosition() {
		return positionProvider.get();
	}

	@Override
	PlayerCharacter getCharacter(Vector2 origin) {
		return characterSupplier.apply(origin);
	}
}
