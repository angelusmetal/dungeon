package com.dungeon.engine.ui.widget;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class HLayout extends AbstractWidget implements Widget {

	public enum Alignment { TOP, BOTTOM;}

	private final List<Widget> children = new ArrayList<>();
	private int padding;
	private Alignment alignment;

	public HLayout pad(int padding) {
		this.padding = padding;
		return this;
	}

	public HLayout align(Alignment alignment) {
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
		widget.setX(x + width);
		widget.setY(alignment == Alignment.BOTTOM ? y : y + height - widget.getHeight());
		width += widget.getWidth() + padding;
	}

	private void reset() {
		width = 0;
		height = children.stream().map(Widget::getHeight).max(Integer::compareTo).orElse(0);
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
