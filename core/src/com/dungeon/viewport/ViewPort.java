package com.dungeon.viewport;

public class ViewPort {
	public final int width;
	public final int height;
	public float scale = 4;
	public int xOffset = 0;
	public int yOffset = 0;

	public ViewPort(int width, int height, int xOffset, int yOffset, float scale) {
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.scale = scale;
	}

	@Override
	public String toString() {
		return "ViewPort{" +
				"width=" + width +
				", height=" + height +
				", scale=" + scale +
				", xOffset=" + xOffset +
				", yOffset=" + yOffset +
				'}';
	}
}
