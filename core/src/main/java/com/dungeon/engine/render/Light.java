package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Light {

	/** Light texture to use */
	public final Sprite sprite;
	/** Light color as a Color (red, green, blue, alpha) */
	public final Color color;
	/** Light diameter, in units */
	public final float diameter;
	/** Light dim; affects intensity of color and diameter */
	public float dim = 1f;
	/** Light angle */
	public float angle = 0f;
	/** Light offset; used to offset the light from the entity origin */
	public Vector2 offset;
	/** Light displacement; used for randomly displacing lights a bit from their origin (e.g. torchlight effect) */
	public Vector2 displacement = new Vector2();
	public final boolean castsShadow;
	/** Whether texture should be rendered twice (mirrored, the second time). Only applies to flares */
	public final boolean mirror;

	private final List<Consumer<Light>> traits;

	public Light(LightPrototype prototype) {
		this.sprite = prototype.sprite;
		this.color = prototype.color.cpy();
		this.diameter = prototype.diameter;
		this.offset = prototype.offset;
		this.traits = prototype.traits.stream().map(Supplier::get).collect(Collectors.toList());
		this.castsShadow = prototype.castsShadow;
		this.mirror = prototype.mirror;
	}

	private Light(float diameter, Color color, Sprite sprite, Vector2 offset, List<Consumer<Light>> traits, boolean castsShadow, boolean mirror) {
		this.sprite = sprite;
		this.color = color;
		this.diameter = diameter;
		this.angle = 0;
		this.offset = offset.cpy();
		this.traits = traits;
		this.castsShadow = castsShadow;
		this.mirror = mirror;
	}

	public Light cpy() {
		return new Light(diameter, color.cpy(), sprite, offset, traits, castsShadow, mirror);
	}

	public void update() {
		traits.forEach(t -> t.accept(this));
	}

}
