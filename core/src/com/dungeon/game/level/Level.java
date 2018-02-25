package com.dungeon.game.level;

import com.dungeon.engine.render.Tile;

import java.util.List;

public class Level {
	public TileType[][] walkableTiles;
	public Tile[][] map;
	public List<Room> rooms;
}
