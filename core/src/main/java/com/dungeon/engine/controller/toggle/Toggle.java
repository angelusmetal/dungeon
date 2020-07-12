package com.dungeon.engine.controller.toggle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Toggle {

	private List<Consumer<Boolean>> listeners = new ArrayList<>();

	public void addListener(Consumer<Boolean> listener) {
		listeners.add(listener);
	}

	public void notifyListeners(boolean pressed) {
		for (Consumer<Boolean> listener : listeners) {
			listener.accept(pressed);
		}
	}

}
