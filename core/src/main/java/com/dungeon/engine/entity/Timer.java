package com.dungeon.engine.entity;

import com.dungeon.engine.Engine;

public class Timer {
	private final float interval;
	private float expiration;

	public Timer(float interval) {
		this.interval = interval;
		this.expiration = Engine.time() + interval;
	}
	public void doAtInterval(Runnable runnable) {
		if (Engine.time() > expiration) {
			runnable.run();
			expiration += interval;
		}
	}
}
