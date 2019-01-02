package com.dungeon.engine.ui.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Widget {
	/** Get the left coordinate */
	int getX();
	/** Set the left coordinate, and update children, if any */
	void setX(int x);
	/** Get the bottom coordinate */
	int getY();
	/** Set the bottom coordinate, and update children, if any */
	void setY(int y);
	/** Get the width */
	int getWidth();
	/** Get the height */
	int getHeight();
	/** Draw the widget on the supplied batch */
	void draw(SpriteBatch batch);
	/** Subscribe an object to listen to size changes in this widget */
	void setSizeObserver(Runnable observer);
	/** Get a vector describing the center of the widget */
	default Vector2 getCenter() {
		return new Vector2(getX() + getWidth() / 2f, getY() - getHeight() / 2f);
	}
}
