package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Light {

	/** Light texture to use */
	public final Texture texture;
	/** Light color as a Color (red, green, blue, alpha) */
	public final Color color;
	/** Light diameter, in units */
	public final float diameter;
	/** Light dim; affects intensity of color and diameter */
	public float dim = 1;
	/** Light angle */
	public float angle;

	private final List<Consumer<Light>> traits;

	public Light(float diameter, Color color, Texture texture) {
		this(diameter, color, texture, Collections.emptyList());
	}

	public Light(float diameter, Color color, Texture texture, Consumer<Light> trait) {
		this(diameter, color, texture, Collections.singletonList(trait));
	}

	public Light(float diameter, Color color, Texture texture, Consumer<Light> trait1, Consumer<Light> trait2) {
		this(diameter, color, texture, Arrays.asList(trait1, trait2));
	}

	public Light(float diameter, Color color, Texture texture, Consumer<Light> trait1, Consumer<Light> trait2, Consumer<Light> trait3) {
		this(diameter, color, texture, Arrays.asList(trait1, trait2, trait3));
	}

	public Light(float diameter, Color color, Texture texture, Consumer<Light> trait1, Consumer<Light> trait2, Consumer<Light> trait3, Consumer<Light> trait4) {
		this(diameter, color, texture, Arrays.asList(trait1, trait2, trait3, trait4));
	}

	public Light(float diameter, Color color, Texture texture, List<Consumer<Light>> traits) {
		this.texture = texture;
		this.color = color;
		this.diameter = diameter;
		this.angle = 0;
		this.traits = traits;
	}

	public Light cpy() {
		return new Light(diameter, color.cpy(), texture, traits);
	}

	public void update() {
		traits.forEach(t -> t.accept(this));
	}

	public static Supplier<Consumer<Light>> torchlight() {
		return torchlight(0.05f);
	}

	public static Supplier<Consumer<Light>> torchlight(float delta) {
		float min = 1 - delta;
		float max = 1 + delta;
		Metronome metronome = new Metronome(delta);
		return () -> light -> metronome.doAtInterval(() -> light.dim = Rand.between(min, max));
	}

	public static Supplier<Consumer<Light>> oscillate() {
		return oscillate(0.5f, 1.5f);
	}

	public static Supplier<Consumer<Light>> oscillate(float delta, float frequency) {
		return () -> light -> light.dim = 1 + MathUtils.sin(Engine.time() * frequency) * delta;
	}

	public static Supplier<Consumer<Light>> rotateSlow() {
		return () -> light -> light.angle = Engine.time() % 360 * 20;
	}

	public static Supplier<Consumer<Light>> rotateMedium() {
		return () -> light -> light.angle = Engine.time() % 360 * 50;
	}

	public static Supplier<Consumer<Light>> rotateFast() {
		return () -> light -> light.angle = Engine.time() % 360 * 80;
	}
}
