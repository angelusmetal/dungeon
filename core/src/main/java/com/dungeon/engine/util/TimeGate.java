package com.dungeon.engine.util;

import com.dungeon.engine.Engine;

/**
 * A time based gate where actions are attempted; successful actions will lock the gate for a fixed amount of time.
 */
public class TimeGate {
	private float gate = 0f;

	/**
	 * Attempt action; if gate is isOpen, the action will be executed and the gate will close for the specified amount of
	 * time. Otherwise, the action will not be executed.
	 * @param time Amount of time to close the gate after (if) the action is executed.
	 * @param action Action to execute (only if gate is isOpen).
	 */
	public void attempt(float time, Runnable action) {
		if (Engine.time() > gate) {
			gate = Engine.time() + time;
			action.run();
		}
	}

	public boolean isOpen() {
		return Engine.time() > gate;
	}
}
