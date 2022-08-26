package com.dungeon.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.pov.PovDirection;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;

import java.util.EnumMap;

public abstract class ControlBundle {

	private static final ControlBundleListener NO_OP = new AbstractControlBundleListener() {};

	private final EnumMap<Game.State, ControlBundleListener> listeners;
	private PlayerEntity entity;

	public ControlBundle() {
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

	public void addStateListener(Game.State state, ControlBundleListener listener) {
		listeners.put(state, listener);
	}

	void updateDirection(Vector2 vector) {
		listeners.get(Game.getCurrentState()).updateDirection(vector);
	}

	void updateAim(Vector2 vector) {
		listeners.get(Game.getCurrentState()).updateAim(vector);
	}

	void povTrigger(PovDirection pov) {
		listeners.get(Game.getCurrentState()).povTrigger(pov);
	}

	void toggleA(boolean on) {
		listeners.get(Game.getCurrentState()).toggleA(on);
	}

	void toggleB(boolean on) {
		listeners.get(Game.getCurrentState()).toggleB(on);
	}

	void toggleX(boolean on) {
		listeners.get(Game.getCurrentState()).toggleX(on);
	}

	void toggleY(boolean on) {
		listeners.get(Game.getCurrentState()).toggleY(on);
	}

	void toggleL1(boolean on) {
		listeners.get(Game.getCurrentState()).toggleL1(on);
	}

	void toggleL2(boolean on) {
		listeners.get(Game.getCurrentState()).toggleL2(on);
	}

	void toggleR1(boolean on) {
		listeners.get(Game.getCurrentState()).toggleR1(on);
	}

	void toggleR2(boolean on) {
		listeners.get(Game.getCurrentState()).toggleR2(on);
	}

	void triggerA() {
		listeners.get(Game.getCurrentState()).triggerA();
	}

	void triggerB() {
		listeners.get(Game.getCurrentState()).triggerB();
	}

	void triggerX() {
		listeners.get(Game.getCurrentState()).triggerX();
	}

	void triggerY() {
		listeners.get(Game.getCurrentState()).triggerY();
	}

	void triggerL1() {
		listeners.get(Game.getCurrentState()).triggerL1();
	}

	void triggerL2() {
		listeners.get(Game.getCurrentState()).triggerL2();
	}

	void triggerR1() {
		listeners.get(Game.getCurrentState()).triggerR1();
	}

	void triggerR2() {
		listeners.get(Game.getCurrentState()).triggerR2();
	}

}
