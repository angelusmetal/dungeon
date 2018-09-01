package com.dungeon.game.controller;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;

import java.util.EnumMap;

public abstract class PlayerControlBundle {

	public interface Listener {
		/** Update analog direction */
		void updateDirection(Vector2 vector);
		/** POV trigger */
		void povTrigger(PovDirection pov);
		/** Toggle 1 */
		void toggle1(boolean on);
		/** Toggle 2 */
		void toggle2(boolean on);
		/** Toggle 3 */
		void toggle3(boolean on);
		/** Toggle 4 */
		void toggle4(boolean on);
		/** Trigger 1 */
		void trigger1();
		/** Trigger 2 */
		void trigger2();
		/** Trigger 3 */
		void trigger3();
		/** Trigger 4 */
		void trigger4();
	}

	private static final Listener NO_OP = new Listener() {
		@Override public void updateDirection(Vector2 vector) {}
		@Override public void povTrigger(PovDirection pov) {}
		@Override public void toggle1(boolean on) {}
		@Override public void toggle2(boolean on) {}
		@Override public void toggle3(boolean on) {}
		@Override public void toggle4(boolean on) {}
		@Override public void trigger1() {}
		@Override public void trigger2() {}
		@Override public void trigger3() {}
		@Override public void trigger4() {}
	};

	private final EnumMap<Game.State, Listener> listeners;
	private PlayerEntity entity;

	public PlayerControlBundle() {
		this.listeners = new EnumMap<>(Game.State.class);
		for (Game.State s : Game.State.values()) {
			listeners.put(s, NO_OP);
		}
	}

	public void setEntity(PlayerEntity entity) {
		this.entity = entity;
	}

	public PlayerEntity getEntity() {
		return entity;
	}

	public void addStateListener(Game.State state, Listener listener) {
		listeners.put(state, listener);
	}

	void updateDirection(Vector2 vector) {
		listeners.get(Game.getCurrentState()).updateDirection(vector);
	}

	void povTrigger(PovDirection pov) {
		listeners.get(Game.getCurrentState()).povTrigger(pov);
	}

	void toggle1(boolean on) {
		listeners.get(Game.getCurrentState()).toggle1(on);
	}

	void toggle2(boolean on) {
		listeners.get(Game.getCurrentState()).toggle2(on);
	}

	void toggle3(boolean on) {
		listeners.get(Game.getCurrentState()).toggle3(on);
	}

	void toggle4(boolean on) {
		listeners.get(Game.getCurrentState()).toggle4(on);
	}

	void trigger1() {
		listeners.get(Game.getCurrentState()).trigger1();
	}

	void trigger2() {
		listeners.get(Game.getCurrentState()).trigger2();
	}

	void trigger3() {
		listeners.get(Game.getCurrentState()).trigger3();
	}

	void trigger4() {
		listeners.get(Game.getCurrentState()).trigger4();
	}

}
