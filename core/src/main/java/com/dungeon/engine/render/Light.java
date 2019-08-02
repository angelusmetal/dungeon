package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
	public float angle = 0;
	/** Light offset */
	public Vector2 offset = new Vector2();

	private final List<Consumer<Light>> traits;

	public Light(LightPrototype prototype) {
		this.texture = prototype.texture;
		this.color = prototype.color;
		this.diameter = prototype.diameter;
		this.traits = prototype.traits.stream().map(Supplier::get).collect(Collectors.toList());
	}

	public Light(float diameter, Color color, Texture texture, List<Consumer<Light>> traits) {
		this.texture = texture;
		this.color = color;
		this.diameter = diameter;
		this.angle = 0;
		this.traits = traits;
	}

	public Light cpy() {
		// TODO Sounds like copying traits can cause problems...
		return new Light(diameter, color.cpy(), texture, traits);
	}

	public void update() {
		traits.forEach(t -> t.accept(this));
	}

}
