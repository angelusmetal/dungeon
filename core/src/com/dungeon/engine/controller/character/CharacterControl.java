package com.dungeon.engine.controller.character;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.GameState;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Controls a character through various simpler controls (such as directional, trigger) to govern how/when a character
 * moves, fires, starts, etc.
 *
 * Each separate implementation will have a different backend (such as keyboard, controller, etc)
 *
 * TODO There should actually be one implementation per character class
 */
public class CharacterControl {

	private static enum CharacterType {
		WITCH, THIEF, ASSASIN
	}

	private final GameState state;
	private final Supplier<Vector2> positionProvider;
	private final Function<Vector2, PlayerCharacter> characterSupplier;
	private PlayerCharacter character;

	public CharacterControl(GameState state, Supplier<Vector2> positionProvider, Function<Vector2, PlayerCharacter> characterSupplier) {
		this.state = state;
		this.positionProvider = positionProvider;
		this.characterSupplier = characterSupplier;
	}

	public void start() {
		if (character == null || character.isExpired(state.getStateTime())) {
			Vector2 startingPosition = getStartingPosition();
			character = getCharacter(new Vector2(startingPosition.x * state.getLevelTileset().tile_size, startingPosition.y * state.getLevelTileset().tile_size));
			state.addPlayerCharacter(character);
		}
	}

	public void updateDirection(PovDirection pov, Vector2 vector) {
		if (character != null) {
			character.setSelfMovement(vector);
		}
	}

	public void fire() {
		if (character != null) {
			character.fire(state);
		}
	}

	Vector2 getStartingPosition() {
		return positionProvider.get();
	}

	PlayerCharacter getCharacter(Vector2 origin) {
		return characterSupplier.apply(origin);
	}
}
