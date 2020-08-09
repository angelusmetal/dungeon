package com.dungeon.desktop.shader;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Segments {

	public static void rectangle(List<Float> geometry, float left, float right, float bottom, float top) {
		geometry.add(left);
		geometry.add(bottom);
		geometry.add(right);
		geometry.add(bottom);

		geometry.add(right);
		geometry.add(bottom);
		geometry.add(right);
		geometry.add(top);

		geometry.add(right);
		geometry.add(top);
		geometry.add(left);
		geometry.add(top);

		geometry.add(left);
		geometry.add(top);
		geometry.add(left);
		geometry.add(bottom);
	}

	public static void circle(List<Float> geometry, Vector2 origin, float radius, int segments) {
		Vector2 step = new Vector2(0, radius);
		Vector2 vertex = origin.cpy().add(step);
		for (int i = 0; i < segments; ++i) {
			geometry.add(vertex.x);
			geometry.add(vertex.y);
			step.rotate(360f / segments);
			vertex.set(origin).add(step);
			geometry.add(vertex.x);
			geometry.add(vertex.y);
		}
	}


}
