package com.dungeon.engine.controller.pov;

import com.badlogic.gdx.controllers.PovDirection;

import java.util.ArrayList;
import java.util.List;

public class PovTrigger implements PovToggleListener {

	private PovDirection direction = PovDirection.center;
	private List<PovTriggerListener> listeners = new ArrayList<>();

	public PovTrigger(PovToggle povToggle) {
		povToggle.addListener(this);
	}

	public void addListener(PovTriggerListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners(PovDirection direction) {
		for (PovTriggerListener listener : listeners) {
			listener.onTrigger(direction);
		}
	}

	@Override
	public void onUpdate(PovDirection direction) {
		if (direction != this.direction) {
			this.direction = direction;
			notifyListeners(direction);
		}
	}
}
