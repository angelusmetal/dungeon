package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class Room {
	List<ProceduralLevelGenerator.ConnectionPoint> connectionPoints;
	public final int left;
	public final int bottom;
	public final int width;
	public final int height;
	public final int generation;
	public final List<Vector2> spawnPoints = new ArrayList<>();
	public final List<EntityPlaceholder> placeholders = new ArrayList<>();

	public final TileType[][] tiles;

	public Room(int left, int bottom, int width, int height, int generation) {
		tiles = new TileType[width][height];
		this.left = left;
		this.bottom = bottom;
		this.width = width;
		this.height = height;
		this.generation = generation;
	}

	@Override
	public String toString() {
		return "[" + generation + "] left: " + left + ", bottom: " + bottom + ", width: " + width + ", height: " + height + ", connectionPoints: [" + connectionPoints + "]";
	}
}
