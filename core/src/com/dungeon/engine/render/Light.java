package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;

import java.util.function.Supplier;

public class Light {

	public static Texture NORMAL_TEXTURE = ResourceManager.getTexture("light_1.png");
	public static Texture RAYS_TEXTURE = ResourceManager.getTexture("light_2.png");
	public static Texture FLARE_TEXTURE = ResourceManager.getTexture("light_3.png");
	public static Texture POSTERIZED_TEXTURE = ResourceManager.getTexture("light_cellshaded.png");

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
			torchlight = Rand.between(0.95f, 1.05f);

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
	public float diameter;
	/** Describes the light color as a Color (red, green, blue, alpha) */
	public final Color color;
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

	public Light(float diameter, Color color, Texture texture, Supplier<Float> dimmer, Supplier<Float> rotator) {
		this.diameter = diameter;
		this.color = color;
		this.texture = texture;
		this.dimmer = dimmer;
		this.rotator = rotator;
	}

	public Light cpy() {
		return new Light(diameter, color.cpy(), texture, dimmer, rotator);
	}
}
