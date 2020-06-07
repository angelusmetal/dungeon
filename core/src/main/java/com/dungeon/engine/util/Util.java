package com.dungeon.engine.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Util {
	public static float clamp(float val) {
		return Math.max(0f, Math.min(1f, val));
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}

	public static float step(float edge, float val) {
		return val < edge ? 0f : 1f;
	}

	public static float smoothstep(float edge0, float edge1, float val) {
		if (val < edge0) {
			return 0f;
		} else if (val > edge1) {
			return 1f;
		} else {
			return (val - edge0) / (edge1 - edge0);
		}
	}

	public static Color hsvaToColor(float hue, float saturation, float value, float alpha) {

		int h = (int)(hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
			case 0: return new Color(value, t, p, alpha);
			case 1: return new Color(q, value, p, alpha);
			case 2: return new Color(p, value, t, alpha);
			case 3: return new Color(p, q, value, alpha);
			case 4: return new Color(t, p, value, alpha);
			case 5: return new Color(value, p, q, alpha);
			default: throw new RuntimeException("Invalid HSV conversion:  " + hue + ", " + saturation + ", " + value);
		}
	}

	public static Vector2 floor(Vector2 vector2) {
		vector2.x = (int) vector2.x;
		vector2.y = (int) vector2.y;
		return vector2;
	}

	public static float length2(float length) {
		return length * length;
	}

	public static Vector2 randVect(float length) {
		return new Vector2(length, 0).rotate(Rand.nextFloat(360));
	}
	public static Vector2 randVect(float minLength, float maxLength) {
		return new Vector2(Rand.between(minLength, maxLength), 0).rotate(Rand.nextFloat(360));
	}

	public static String nanosToString(int nanos) {
		if (nanos < 1000) {
			return nanos + " ns";
		} else if (nanos < 1000_000) {
			return (nanos / 1000) + " us";
		} else if (nanos < 1000_000_000) {
			return (nanos / 1000_000) + " ms";
		} else {
			return (nanos / 1000_000_000) + " s";
		}
	}

	public static String nanosToString(double nanos) {
		if (nanos < 1000) {
			return (int) nanos + " ns";
		} else if (nanos < 1000_000) {
			return (int) (nanos / 1000) + " us";
		} else if (nanos < 1000_000_000) {
			return (int) (nanos / 1000_000) + " ms";
		} else {
			return (int) (nanos / 1000_000_000) + " s";
		}
	}

}
