package com.dungeon.engine.controller.player;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.state.GameState;

import java.util.EnumMap;

public abstract class PlayerControl {

	public interface PlayerControlListener {
		void updateDirection(PovDirection pov, Vector2 vector);
		void start();
		void trigger1();
		void trigger2();
		void trigger3();
		void trigger4();
	}

	private static final PlayerControlListener NO_OP = new PlayerControlListener() {
		@Override public void updateDirection(PovDirection pov, Vector2 vector) {}
		@Override public void start() {}
		@Override public void trigger1() {}
		@Override public void trigger2() {}
		@Override public void trigger3() {}
		@Override public void trigger4() {}
	};

	private final GameState state;
	private final EnumMap<GameState.State, PlayerControlListener> listeners;
	private PlayerCharacter character;

	public PlayerControl(GameState state) {
		this.state = state;
		this.listeners = new EnumMap<>(GameState.State.class);
		for (GameState.State s : GameState.State.values()) {
			listeners.put(s, NO_OP);
		}
	}

	public void setCharacter(PlayerCharacter character) {
		this.character = character;
	}

	public PlayerCharacter getCharacter() {
		return character;
	}

	public void addStateListener(GameState.State state, PlayerControlListener listener) {
		listeners.put(state, listener);
	}

	void updateDirection(PovDirection pov, Vector2 vector) {
		listeners.get(state.getCurrentState()).updateDirection(pov, vector);
////		CharacterSelection selection;
//		if (state.getCurrentState() == GameState.State.INGAME) {
//			characterControl.updateDirection(pov, vector);
////		} else if (state.getCurrentState() == GameState.State.MENU) {
////			// TODO Create a directional trigger?
////			selection.selectNextCharacter();
//		}
	}

	void start() {
		listeners.get(state.getCurrentState()).start();
	}

	void trigger1() {
		listeners.get(state.getCurrentState()).trigger1();
	}

	void trigger2() {
		listeners.get(state.getCurrentState()).trigger2();
	}

	void trigger3() {
		listeners.get(state.getCurrentState()).trigger3();
	}

	void trigger4() {
		listeners.get(state.getCurrentState()).trigger4();
	}

}
