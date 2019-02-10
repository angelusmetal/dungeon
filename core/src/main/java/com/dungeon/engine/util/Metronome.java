package com.dungeon.engine.util;

import com.dungeon.engine.Engine;

public class Metronome {
	private final float interval;
	private final Runnable action;
	private float expiration;

	public Metronome(float interval, Runnable action) {
		this.interval = interval;
		this.action = action;
		this.expiration = Engine.time() + interval;
	}

	/**
	 * Perform the action if it's time
	 */
	public void doAtInterval() {
		if (Engine.time() > expiration) {
			action.run();
			expiration = Engine.time() + interval;
		}
	}
}
