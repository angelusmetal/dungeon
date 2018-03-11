package com.dungeon.game.state;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.player.PlayerControl;
import com.dungeon.engine.entity.PlayerCharacter;

public class CharacterPlayerControlListener implements PlayerControl.PlayerControlListener {

	private final PlayerControl control;
	private final GameState state;

	public CharacterPlayerControlListener(PlayerControl control, GameState state) {
		this.control = control;
		this.state = state;
	}

	@Override
	public void updateDirection(PovDirection pov, Vector2 vector) {
		PlayerCharacter character = control.getCharacter();
		if (character != null) {
			character.setSelfMovement(vector);
		}
	}

	@Override
	public void start() {
//		if (character == null || character.isExpired(state.getStateTime())) {
//			Vector2 startingPosition = positionProvider.get();
//			character = characterSupplier.apply(new Vector2(startingPosition.x * state.getLevelTileset().tile_size, startingPosition.y * state.getLevelTileset().tile_size));
//			state.addPlayerCharacter(character);
//		}
	}

	@Override
	public void trigger1() {
		PlayerCharacter character = control.getCharacter();
		if (character != null) {
			character.fire(state);
		}
	}

	@Override
	public void trigger2() {
		trigger1();
	}

	@Override
	public void trigger3() {
		trigger1();
	}

	@Override
	public void trigger4() {
		trigger1();
	}

}
