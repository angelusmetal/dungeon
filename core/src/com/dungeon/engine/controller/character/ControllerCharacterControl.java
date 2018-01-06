package com.dungeon.engine.controller.character;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.trigger.ControllerTriggerControl;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.GameState;

import java.util.function.Supplier;

public class ControllerCharacterControl extends CharacterControl {

	private final GameState state;
	private final Controller controller;
	private final Supplier<Vector2> positionProvider;
	private final Supplier<PlayerCharacter> characterSupplier;
	private final AnalogDirectionalControl directionalControl;
	private final ControllerTriggerControl fireControl;
	private final ControllerTriggerControl startTrigger;

	public ControllerCharacterControl(GameState state, Controller controller, Supplier<Vector2> positionProvider, Supplier<PlayerCharacter> characterSupplier) {
		this.state = state;
		this.controller = controller;
		this.positionProvider = positionProvider;
		this.characterSupplier = characterSupplier;
		this.directionalControl = new AnalogDirectionalControl(3, -2);
		this.fireControl = new ControllerTriggerControl(0);
		this.startTrigger = new ControllerTriggerControl(9);

		directionalControl.addListener((pov, vec) -> character.setSelfMovement(vec));
		fireControl.addListener((bool) -> {if (bool) character.fire(state);});
		startTrigger.addListener((bool) -> bind());
		controller.addListener(startTrigger);
	}

	@Override
	public void bind() {
		if (character == null|| character.isExpired(state.getStateTime())) {
			Vector2 startingPosition = positionProvider.get();
			character = characterSupplier.get();
			character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
			state.addPlayerCharacter(character);
			controller.addListener(directionalControl);
			controller.addListener(fireControl);
		}
	}

	@Override
	public void unbind() {
		if (character != null) {
			character = null;
			// TODO We need to unregister previous control listeners or leak references to the old character
			controller.removeListener(directionalControl);
			controller.removeListener(fireControl);
		}
	}
}
