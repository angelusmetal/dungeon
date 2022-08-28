package com.dungeon.engine.render.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.util.Objects;

import static java.lang.Math.pow;

public class RenderLight {
	private final Vector3 origin;
	private float range;
	private float radius;
	private final Color color;
	private final boolean castsShadows;
	private float hardness;
	private float decay;

	/**
	 * Create a light for rendering
	 * @param origin Origin of the light
	 * @param range How many units it reaches
	 * @param radius Radius of the light emitting body
	 * @param color Color of light
	 * @param castsShadows Whether this light casts stencil shadows (more expensive, as it uses an intermediate buffer)
	 */
	public RenderLight(Vector3 origin, float range, float radius, Color color, boolean castsShadows) {
		this(origin, range, radius, color, 0.5f, castsShadows);
	}

	/**
	 * Create a light for rendering
	 * @param origin Origin of the light
	 * @param range How many units it reaches
	 * @param radius Radius of the light emitting body
	 * @param color Color of light
	 * @param castsShadows Whether this light casts stencil shadows (more expensive, as it uses an intermediate buffer)
	 */
	public RenderLight(Vector3 origin, float range, float radius, Color color, float hardness, boolean castsShadows) {
		this.origin = origin.cpy();
		this.range = range;
		this.radius = radius;
		this.color = color;
		setHardness(hardness);
		this.castsShadows = castsShadows;
	}

	public Vector3 getOrigin() {
		return origin;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getHardness() {
		return hardness;
	}

	public void setHardness(float hardness) {
		this.hardness = hardness;
		this.decay = 2f + 35f * hardness + (float) pow(0.5f * hardness, 8f) * 490f;
	}

	public float getDecay() {
		return decay;
	}

	public Color getColor() {
		return color;
	}

	public boolean castsShadows() {
		return castsShadows;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RenderLight that = (RenderLight) o;
		return Float.compare(that.radius, radius) == 0 && Float.compare(that.range, range) == 0 && castsShadows == that.castsShadows && origin.equals(that.origin) && Objects.equals(color, that.color);
	}

	@Override
	public int hashCode() {
		return Objects.hash(origin, radius, range, color, castsShadows);
	}

	@Override
	public String toString() {
		return "Light{" +
				"origin=" + origin +
				", range=" + range +
				", radius=" + radius +
				", color=" + color +
				'}';
	}
}
