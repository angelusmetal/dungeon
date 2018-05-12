package com.dungeon.engine.util;

import com.badlogic.gdx.graphics.Color;

public class Util {
	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
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

}
