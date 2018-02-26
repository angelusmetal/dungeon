package com.dungeon.game.level;

import java.util.List;

public class Room {
	List<ProceduralLevelGenerator.ConnectionPoint> connectionPoints;
	public final int left;
	public final int bottom;
	public final int width;
	public final int height;
	public final int startX;
	public final int startY;

	public final TileType[][] tiles;

	public Room(int left, int bottom, int width, int height, int startX, int startY) {
		tiles = new TileType[width][height];
		this.left = left;
		this.bottom = bottom;
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
	}

	@Override
	public String toString() {
		return "left: " + left + ", bottom: " + bottom + ", width: " + width + ", height: " + height + ", connectionPoints: [" + connectionPoints + "]";
	}
}
