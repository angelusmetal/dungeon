package com.dungeon.engine.render;

import com.badlogic.gdx.math.Quaternion;

import java.util.function.Supplier;

public class Light {

	static final float thinkInterval = 0.05f;
	static float nextThink = 0;
	private static float torchlight = 1;

	private static double angle = 0;
	private static float sine = 1;

	static public void updateDimmers(float time) {
		nextThink += time;
		if (nextThink >= thinkInterval) {
			nextThink = 0;
			torchlight = 0.95f + (float) (Math.random() * 0.1f);

			angle += thinkInterval;
			sine = (float) Math.sin(angle);
		}
	}

	static public float torchlight() {
		return torchlight;
	}

	static public float sine() {
		return sine;
	}

	/** Describes the base light radius, in units */
	public final float radius;
	/** Describes the light color as a Quaternion (red, green, blue, alpha) */
	public final Quaternion color;
	/**
	 * Describes the dim supplier to make the light fluctuate.
	 * The supplied value is a scale that is applied to both alpha and radius.
	 */
	public final Supplier<Float> dim;

	public Light(float radius, Quaternion color, Supplier<Float> dim) {
		this.radius = radius;
		this.color = color;
		this.dim = dim;
	}
}
