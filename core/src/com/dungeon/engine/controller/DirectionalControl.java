package com.dungeon.engine.controller;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * A directional control; notifies registered listeners.
 */
public abstract class DirectionalControl {

	private List<DirectionalListener> listeners = new ArrayList<>();

	public void addListener(DirectionalListener listener) {
		listeners.add(listener);
	}

	protected void updateListeners(PovDirection povDirection, Vector2 vectorDirection) {
		for (DirectionalListener listener : listeners) {
			listener.onUpdate(povDirection, vectorDirection);
		}
	}

}
