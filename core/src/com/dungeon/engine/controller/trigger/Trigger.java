package com.dungeon.engine.controller.trigger;

import com.dungeon.engine.controller.toggle.Toggle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Trigger implements Consumer<Boolean> {

	private List<Runnable> listeners = new ArrayList<>();

	public Trigger(Toggle toggle) {
		toggle.addListener(this);
	}

	public void addListener(Runnable listener) {
		listeners.add(listener);
	}

	protected void notifyListeners() {
		for (Runnable listener : listeners) {
			listener.run();
		}
	}

	@Override
	public void accept(Boolean bool) {
		if (bool) {
			notifyListeners();
		}
	}
}
