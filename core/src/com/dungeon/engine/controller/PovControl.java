package com.dungeon.engine.controller;

import com.badlogic.gdx.controllers.PovDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * A POV control; notifies registered listeners.
 */
public abstract class PovControl {

	private List<PovListener> listeners = new ArrayList<>();

	public void addListener(PovListener listener) {
		listeners.add(listener);
	}

	protected void updateListeners(PovDirection povDirection) {
		for (PovListener listener : listeners) {
			listener.onUpdate(povDirection);
		}
	}

}
