package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Quaternion;

import java.util.function.Supplier;

public class Light {

	public static Texture NORMAL_TEXTURE;
	public static Texture RAYS_TEXTURE;
	public static Texture FLARE_TEXTURE;
	public static Texture POSTERIZED_TEXTURE;

	public static void initialize() {
		NORMAL_TEXTURE = new Texture("light_1.png");
		RAYS_TEXTURE = new Texture("light_2.png");
		FLARE_TEXTURE = new Texture("light_3.png");
		POSTERIZED_TEXTURE = new Texture("light_cellshaded.png");
	}

	public static void dispose() {
		NORMAL_TEXTURE.dispose();
		RAYS_TEXTURE.dispose();
		FLARE_TEXTURE.dispose();
		POSTERIZED_TEXTURE.dispose();
	}

	private static final float thinkInterval = 0.05f;
	private static float nextThink = 0;
	private static float torchlight = 1;

	private static float angle = 0;
	private static float sine = 0;
	private static float oscillating = 0;

	public static void updateDimmers(float time) {
		nextThink += time;
		if (nextThink >= thinkInterval) {
			nextThink = 0;
			torchlight = 0.95f + (float) (Math.random() * 0.1f);

			angle += thinkInterval;
			sine = (float) Math.sin(angle);
			oscillating = 1 + sine * 0.5f;
		}
	}

	public static float torchlight() {
		return torchlight;
	}

	public static float sine() {
		return sine;
	}

	public static float oscillating() {
		return oscillating;
	}

	public static float noRotate() {
		return 0;
	}

	public static float rotateSlow() {
		return angle % 360 * 20;
	}

	public static float rotateMedium() {
		return angle % 360 * 50;
	}

	public static float rotateFast() {
		return angle % 260 * 80;
	}

	/** Describes the base light diameter, in units */
	public final float diameter;
	/** Describes the light color as a Quaternion (red, green, blue, alpha) */
	public final Quaternion color;
	/** Light texture to use */
	public final Texture texture;
	/**
	 * Dimmer to make the light fluctuate.
	 * The supplied value is a scale that is applied to both alpha and radius.
	 */
	public final Supplier<Float> dimmer;
	/**
	 * Rotator to make the light rotate.
	 * The supplied value is the angle with which to rotate the light image.
	 */
	public final Supplier<Float> rotator;

	public Light(float diameter, Quaternion color, Texture texture, Supplier<Float> dimmer, Supplier<Float> rotator) {
		this.diameter = diameter;
		this.color = color;
		this.texture = texture;
		this.dimmer = dimmer;
		this.rotator = rotator;
	}
}
