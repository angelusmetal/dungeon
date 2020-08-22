package com.dungeon.engine.render.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class Light2 {
	private Vector2 origin;
	private float z;
	private float radius;
	private float range;
	private Color color;
	private final boolean castsShadows;

	public Light2(Vector2 origin, float z, float radius, float range, Color color, boolean castsShadows) {
		this.origin = origin.cpy();
		this.z = z;
		this.radius = radius;
		this.range = range;
		this.color = color;
		this.castsShadows = castsShadows;
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public float getZ() {
		return z;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
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
		Light2 light = (Light2) o;
		return Float.compare(light.radius, radius) == 0 &&
				Float.compare(light.range, range) == 0 &&
				Objects.equals(origin, light.origin) &&
				Objects.equals(color, light.color);
	}

	@Override
	public int hashCode() {
		return Objects.hash(origin, radius, range, color);
	}

	@Override
	public String toString() {
		return "Light{" +
				"origin=" + origin +
				", radius=" + radius +
				", range=" + range +
				", color=" + color +
				'}';
	}
}
