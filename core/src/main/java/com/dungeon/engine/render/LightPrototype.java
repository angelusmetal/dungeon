package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LightPrototype {

	/** Light texture to use */
	protected final Texture texture;
	/** Light color as a Color (red, green, blue, alpha) */
	protected final Color color;
	/** Light diameter, in units */
	protected final float diameter;

	protected final List<Supplier<Consumer<Light>>> traits = new ArrayList<>();

	public LightPrototype(float diameter, Color color, Texture texture) {
		this.texture = texture;
		this.color = color;
		this.diameter = diameter;
	}

	public LightPrototype torchlight() {
		torchlight(0.05f);
		return this;
	}

	public LightPrototype torchlight(float delta) {
		float min = 1 - delta;
		float max = 1 + delta;
		traits.add(() -> {
			Metronome metronome = new Metronome(delta);
			return light -> metronome.doAtInterval(() -> {
				light.dim = Rand.between(min, max);
				light.displacement.set(Rand.between(-1f, 1f), Rand.between(-1f, 1f));
			});
		});
		return this;
	}

	public LightPrototype oscillate() {
		oscillate(0.5f, 1.5f);
		return this;
	}

	public LightPrototype oscillate(float delta, float frequency) {
		traits.add(() -> light -> light.dim = 1 + MathUtils.sin(Engine.time() * frequency) * delta);
		return this;
	}

	public LightPrototype rotateSlow() {
		traits.add(() -> light -> light.angle = Engine.time() % 360 * 20);
		return this;
	}

	public LightPrototype rotateMedium() {
		traits.add(() -> light -> light.angle = Engine.time() % 360 * 50);
		return this;
	}

	public LightPrototype rotateFast() {
		traits.add(() -> light -> light.angle = Engine.time() % 360 * 80);
		return this;
	}
}
