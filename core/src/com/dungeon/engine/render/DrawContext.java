package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface DrawContext {

	DrawContext NONE = new DrawContext() {
		@Override public void set(SpriteBatch batch) {}
		@Override public void unset(SpriteBatch batch) {}
	};

	/** Set the draw context before drawing */
	void set(SpriteBatch batch);
	/** Reset the draw context after drawing */
	void unset(SpriteBatch batch);

}
