package com.dungeon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.viewport.ViewPort;

public interface Drawable {

	TextureRegion getFrame(float stateTime);
	boolean invertX();

	Vector2 getPos();
	Vector2 getDrawOffset();

	void draw(GameState state, SpriteBatch batch, ViewPort viewPort);

}
