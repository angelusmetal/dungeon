package com.dungeon.engine.util;

import java.util.function.BiConsumer;

public class CyclicSampler {

	private final double[] samples;
	private int current = 0;

	public CyclicSampler(int size) {
		this.samples = new double[size];
	}

	public void sample(double s) {
		current++;
		current %= samples.length;
		samples[current] = s;
	}

	public void forEach(BiConsumer<Integer, Double> consumer) {
		int s = 0;
		for (int i = current + 1; i < samples.length; ++i) {
			consumer.accept(s++, samples[i]);
		}
		for (int i = 0; i < current; ++i) {
			consumer.accept(s++, samples[i]);
		}
	}

	public double maxValue() {
		double max = Integer.MIN_VALUE;
		for (int i = 0; i < samples.length; ++i) {
			max = Math.max(max, samples[i]);
		}
		return max;
	}

	public double currentValue() {
		return samples[current];
	}

	public int getSize() {
		return samples.length;
	}
}
