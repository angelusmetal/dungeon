package com.dungeon.engine.ui.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Particle {
	void drawAndUpdate(SpriteBatch batch);
	boolean isExpired();
	void expire();
	Color getColor();
}
