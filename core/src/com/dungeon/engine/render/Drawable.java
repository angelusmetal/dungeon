package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public interface Drawable {

	TextureRegion getFrame();
	boolean invertX();

	Vector2 getPos();
	Vector2 getDrawOffset();
	float getZIndex();

	void draw(SpriteBatch batch, ViewPort viewPort);

}
