package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Drawable {

	Material getFrame();

	Vector2 getOrigin();
	Vector2 getDrawOffset();
	float getZIndex();

	void draw(SpriteBatch batch);

}
