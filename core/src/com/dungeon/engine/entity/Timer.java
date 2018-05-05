package com.dungeon.engine.entity;

import com.dungeon.game.state.GameState;

public class Timer {
	private final float interval;
	private float expiration;

	public Timer(float interval) {
		this.interval = interval;
		this.expiration = GameState.time() + interval;
	}
	public void doAtInterval(Runnable runnable) {
		if (GameState.time() > expiration) {
			runnable.run();
			expiration += interval;
		}
	}
}
