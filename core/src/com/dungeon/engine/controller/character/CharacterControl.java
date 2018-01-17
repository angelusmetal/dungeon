package com.dungeon.engine.controller.character;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.GameState;

/**
 * Controls a character through various simpler controls (such as directional, trigger) to govern how/when a character
 * moves, fires, starts, etc.
 *
 * Each separate implementation will have a different backend (such as keyboard, controller, etc)
 */
public abstract class CharacterControl {
	protected final GameState state;
	protected PlayerCharacter character;

	CharacterControl(GameState state) {
		this.state = state;
	}

	abstract Vector2 getStartingPosition();
	abstract PlayerCharacter getCharacter();

	void start(boolean pressed) {
		if (character == null || character.isExpired(state.getStateTime()) && pressed) {
			Vector2 startingPosition = getStartingPosition();
			character = getCharacter();
			character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_size, startingPosition.y * state.getLevelTileset().tile_size));
			state.addPlayerCharacter(character);
		}
	}

	void updateDirection(PovDirection pov, Vector2 vector) {
		if (character != null) {
			character.setSelfMovement(vector);
		}
	}

	void fire(boolean pressed) {
		if (character != null && pressed) {
			character.fire(state);
		}
	}
}
