package com.dungeon.engine.ui.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Widget {
	int getX();
	void setX(int x);
	int getY();
	void setY(int y);
	int getWidth();
	int getHeight();
	void draw(SpriteBatch batch);
	void setSizeObserver(Runnable observer);
}
