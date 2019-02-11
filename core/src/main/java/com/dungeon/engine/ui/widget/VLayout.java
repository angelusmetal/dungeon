package com.dungeon.engine.ui.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class VLayout extends AbstractWidget implements Widget {

	public enum Alignment { LEFT, RIGHT }

	private final List<Widget> children = new ArrayList<>();
	private int padding;
	private Alignment alignment;

	public VLayout pad(int padding) {
		this.padding = padding;
		return this;
	}

	public VLayout align(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}

	public void add(Widget widget) {
		children.add(widget);
		// If child widget changes its size, recalculate layout
		widget.setSizeObserver(this::reset);
		reset();
	}

	private void place(Widget widget) {
		widget.setY(y + height);
		widget.setX(alignment == Alignment.LEFT ? x : x + width - widget.getWidth());
		height += widget.getHeight() + padding;
	}

	private void reset() {
		height = 0;
		width = children.stream().map(Widget::getWidth).max(Integer::compareTo).orElse(0);
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
