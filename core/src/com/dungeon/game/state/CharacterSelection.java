package com.dungeon.game.state;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.player.PlayerControl;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.character.Assasin;
import com.dungeon.game.character.Thief;
import com.dungeon.game.character.Witch;
import com.dungeon.game.level.Room;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class CharacterSelection {

	private static final int CHARACTER_COUNT = 3;
	private int currentPlayer = 0;
	private final GameState state;
	private List<Slot> slots = new ArrayList<>(4);

	private static class Slot {
		private PlayerControl control;
		private int playerId;
		private int characterId = 0;
		public Slot(PlayerControl control, int playerId) {
			this.control = control;
			this.playerId = playerId;
		}
	}

	public CharacterSelection(GameState state) {
		this.state = state;
	}

	public boolean addControl(PlayerControl control) {
		if (slots.size() < 4) {
			slots.add(new Slot(control, ++currentPlayer));
			System.out.println("Added player!");
			return true;
		} else {
			return false;
		}
	}

	public void selectNextCharacter(PlayerControl control) {
		getSlot(control).ifPresent(s -> {
			s.characterId = (s.characterId + 1) % CHARACTER_COUNT;
			System.out.println("Player " + s.playerId + " switched to character " + s.characterId);
		});
	}

	public void selectPrevCharacter(PlayerControl control) {
		getSlot(control).ifPresent(s -> {
			s.characterId = (s.characterId - 1 + CHARACTER_COUNT) % CHARACTER_COUNT;
			System.out.println("Player " + s.playerId + " switched to character " + s.characterId);
		});
	}

	public void confirmSelection(PlayerControl control) {
		// TODO Only confirm when all active slots have confirmed

		// Get starting room and spawn players there
		Room startingRoom = state.getLevel().rooms.get(0);
		int spawnPoint = 0;
		for (Slot slot : slots) {
			PlayerCharacter character = createCharacter(slot.characterId, startingRoom.spawnPoints.get(spawnPoint++).cpy().scl(state.getLevelTileset().tile_size));
			state.addPlayerCharacter(character);
			slot.control.setCharacter(character);
		}
		state.setCurrentState(GameState.State.INGAME);
	}

	private Optional<Slot> getSlot(PlayerControl control) {
		for (Slot s : slots) {
			if (s.control == control) {
				return Optional.of(s);
			}
		}
		return Optional.empty();
	}

	private PlayerCharacter createCharacter(int characterId, Vector2 origin) {
		if (characterId == 0) {
			return new Witch(state, origin);
		} else if (characterId == 1) {
			return new Thief(state, origin);
		} else {
			return new Assasin(state, origin);
		}

	}

	private Vector2 getStartingPosition() {
		if (state.getPlayerCharacters().isEmpty()) {
			return state.getLevel().rooms.get(0).spawnPoints.get(0).cpy();
		} else {
			Vector2 refPos = state.getPlayerCharacters().get(0).getPos();
			return new Vector2(refPos.x / state.getLevelTileset().tile_size + 1, refPos.y / state.getLevelTileset().tile_size);
		}
	}


//	Any of the registered PlayerControls can add itself to one slot, if available
//	The slot transitions EMPTY -> SELECTING
//	When on the SELECTING state, directional controls select a character and it is displayed
//	When on the SELECTING state, one button (action1?) transitions to SELECTED
//	When on the SELECTED state, one button (action2?) transitions back to selecting
//	When all the non-empty slots are in SELECTED
//	The selection finishes and information is relayed
//	Assigns a PlayerControl to each slot
//	Assigns a PlayerCharacter to each slot
//	The game state transitions to INGAME
}
