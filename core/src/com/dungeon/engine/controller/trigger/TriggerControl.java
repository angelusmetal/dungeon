package com.dungeon.engine.controller.trigger;

import java.util.ArrayList;
import java.util.List;

public class TriggerControl {

	private List<Runnable> listeners = new ArrayList<>();

	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	protected void updateListeners() {
		for (Runnable listener : listeners) {
			listener.run();
		}
	}

}
