package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LightPrototype {

	/** Light texture to use */
	protected final Sprite sprite;
	/** Light color as a Color (red, green, blue, alpha) */
	protected final Color color;
	/** Light diameter, in units */
	protected final float diameter;
	/** Light offset */
	protected final Vector2 offset;

	protected final List<Supplier<Consumer<Light>>> traits = new ArrayList<>();
	protected final boolean castsShadow;
	protected final boolean mirror;

	public LightPrototype(float diameter, Color color, Sprite sprite, Vector2 offset, boolean castsShadow, boolean mirror) {
		this.sprite = sprite;
		this.color = color.cpy();
		this.diameter = diameter;
		this.offset = offset;
		this.castsShadow = castsShadow;
		this.mirror = mirror;
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
				light.displacement.set(Rand.between(-0.3f, 0.3f), Rand.between(-0.3f, 0.3f));
			});
		});
		return this;
	}

	public LightPrototype oscillate() {
		oscillate(0.25f, 1.5f);
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
