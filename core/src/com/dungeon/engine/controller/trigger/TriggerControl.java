package com.dungeon.engine.controller.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TriggerControl {

	private List<Consumer<Boolean>> listeners = new ArrayList<>();

	public void addListener(Consumer<Boolean> listener) {
		listeners.add(listener);
	}

	protected void updateListeners(boolean pressed) {
		for (Consumer<Boolean> listener : listeners) {
			listener.accept(pressed);
		}
	}

}
