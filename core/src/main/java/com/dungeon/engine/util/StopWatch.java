package com.dungeon.engine.util;

public class StopWatch {
	private long start;
	private long elapsed;

	public StopWatch() {
		start();
	}

	public void start() {
		start = System.nanoTime();
	}

	public void stop() {
		elapsed = System.nanoTime() - start;
	}

	public void measure(int iterations, Runnable task) {
		start();
		for (int i = 0; i < iterations; ++i) {
			task.run();
		}
		stop();
	}

	@Override
	public String toString() {
		if (elapsed > 1_000_000_000L) {
			return Double.toString(elapsed / 1_000_000_000d) + " s";
		} else if (elapsed > 1_000_000L) {
			return Double.toString(elapsed / 1_000_000d) + " ms";
		} else if (elapsed > 1_000L) {
			return Double.toString(elapsed / 1_000d) + " us";
		}
		return Long.toString(elapsed) + " ns";
	}
}
