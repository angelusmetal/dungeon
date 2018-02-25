package com.dungeon.game.level;

import java.util.List;

public class Room {
	@Deprecated public final ProceduralLevelGenerator.Coords topLeft = new ProceduralLevelGenerator.Coords(0,0);
	@Deprecated public final ProceduralLevelGenerator.Coords bottomRight = new ProceduralLevelGenerator.Coords(0,0);
	List<ProceduralLevelGenerator.ConnectionPoint> connectionPoints;
	public final int left;
	public final int bottom;
	public final int width;
	public final int height;

	public final TileType[][] tiles;

	public Room(int left, int bottom, int width, int height) {
		topLeft.x = left;
		topLeft.y = bottom + height - 1;
		bottomRight.x = left + width - 1;
		bottomRight.y = bottom;
		tiles = new TileType[width][height];
		this.left = left;
		this.bottom = bottom;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return "topLeft: " + topLeft + ", bottomRight: " + bottomRight + ", connectionPoints: [" + connectionPoints + "]";
	}
}
