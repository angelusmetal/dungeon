package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ColorContext implements DrawContext {

	public static final ColorContext NORMAL = new ColorContext(Color.WHITE);

	private final Color color;

	public ColorContext(Color color) {
		this.color = color;
	}

	public void set(SpriteBatch batch) {
		batch.setColor(color);
	}

	public void unset(SpriteBatch batch) {
		batch.setColor(Color.WHITE);
	}
}
