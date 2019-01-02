package com.dungeon.engine.ui.widget;

public abstract class AbstractWidget implements Widget {

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected Runnable sizeObserver;

	@Override public int getX() {
		return x;
	}

	@Override public void setX(int x) {
		this.x = x;
	}

	@Override public int getY() {
		return y;
	}

	@Override public void setY(int y) {
		this.y = y;
	}

	@Override public int getWidth() {
		return width;
	}

	@Override public int getHeight() {
		return height;
	}

	@Override public void setSizeObserver(Runnable observer) {
		this.sizeObserver = observer;
	}
}
