package com.dungeon.game.level;

import com.dungeon.engine.physics.LevelTiles;
import com.dungeon.engine.render.Tile;
import com.dungeon.game.Game;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.List;

public class Level implements LevelTiles {
	public TileType[][] walkableTiles;
	public Tile[][] map;
	public List<Room> rooms;
	public List<EntityPlaceholder> entityPlaceholders;

	@Override
	public int getTileSize() {
		return Game.getEnvironment().getTileset().tile_size;
	}

	@Override
	public boolean isSolid(int x, int y) {
		return !walkableTiles[x][y].isFloor();
	}
}
