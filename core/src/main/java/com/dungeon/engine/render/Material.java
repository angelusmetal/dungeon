package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A 2d material containing one or more textures
 */
public class Material {
	private final Sprite diffuse;
	private final Sprite normal;

	public enum Layer {
		DIFFUSE,
		NORMAL
	}

	public Material(Sprite diffuse, Sprite normal) {
		this.diffuse = diffuse;
		this.normal = normal;
	}

	public Sprite getDiffuse() {
		return diffuse;
	}

	public Sprite getNormal() {
		return normal;
	}

	public boolean hasNormal() {
		return normal != null;
	}
}
