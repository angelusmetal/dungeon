package com.dungeon.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class HLayout extends AbstractWidget implements Widget {

	private final List<Widget> children = new ArrayList<>();
	private int padding;

	public HLayout pad(int padding) {
		this.padding = padding;
		return this;
	}

	public void add(Widget widget) {
		children.add(widget);
		// If child widget changes its size, recalculate layout
		widget.setSizeObserver(this::reset);
		place(widget);
	}

	private void place(Widget widget) {
		widget.setX(x + width);
		widget.setY(y);
		width += widget.getWidth() + padding;
		height = Math.max(height, widget.getHeight());
	}

	private void reset() {
		width = 0;
		height = 0;
		children.forEach(this::place);
	}

	@Override public void setX(int x) {
		super.setX(x);
		reset();
	}

	@Override public void setY(int y) {
		super.setY(y);
		reset();
	}

	@Override
	public void draw(SpriteBatch batch) {
		children.forEach(widget -> widget.draw(batch));
	}
}
