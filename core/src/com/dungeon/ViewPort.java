package com.dungeon;

public class ViewPort {
	public float scale = 4;
	public int xOffset = 0;
	public int yOffset = 0;

	public ViewPort(int xOffset, int yOffset, float scale) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.scale = scale;
	}

}
