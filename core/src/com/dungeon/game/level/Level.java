package com.dungeon.game.level;

import com.dungeon.engine.render.Tile;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.List;

public class Level {
	public TileType[][] walkableTiles;
	public Tile[][] map;
	public List<Room> rooms;
	public List<EntityPlaceholder> entityPlaceholders;
}
