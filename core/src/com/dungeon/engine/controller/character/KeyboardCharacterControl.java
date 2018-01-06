package com.dungeon.engine.controller.character;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.directional.KeyboardDirectionalControl;
import com.dungeon.engine.controller.trigger.KeyboardTriggerControl;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.GameState;

import java.util.function.Supplier;

public class KeyboardCharacterControl extends CharacterControl {

	private final GameState state;
	private final InputMultiplexer inputMultiplexer;
	private final Supplier<Vector2> positionProvider;
	private final Supplier<PlayerCharacter> characterSupplier;
	private final KeyboardDirectionalControl directionalControl;
	private final KeyboardTriggerControl fireControl;
	private final KeyboardTriggerControl startTrigger;

	public KeyboardCharacterControl(GameState state, InputMultiplexer inputMultiplexer, Supplier<Vector2> positionProvider, Supplier<PlayerCharacter> characterSupplier) {
		this.state = state;
		this.inputMultiplexer = inputMultiplexer;
		this.positionProvider = positionProvider;
		this.characterSupplier = characterSupplier;
		this.directionalControl = new KeyboardDirectionalControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
		this.fireControl = new KeyboardTriggerControl(Input.Keys.SPACE);
		this.startTrigger = new KeyboardTriggerControl(Input.Keys.ENTER);

		directionalControl.addListener((pov, vec) -> character.setSelfMovement(vec));
		fireControl.addListener((bool) -> { if (bool) character.fire(state);});
		startTrigger.addListener((bool) -> bind());
		inputMultiplexer.addProcessor(startTrigger);
	}

	@Override
	public void bind() {
		if (character == null || character.isExpired(state.getStateTime())) {
			Vector2 startingPosition = positionProvider.get();
			character = characterSupplier.get();
			character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
			state.addPlayerCharacter(character);
			inputMultiplexer.addProcessor(directionalControl);
			inputMultiplexer.addProcessor(fireControl);
		}
	}

	@Override
	public void unbind() {
		if (character != null) {
			character.setExpired(state, true);
			// TODO We need to unregister previous control listeners or leak references to the old character
			character = null;
		}
	}
}
