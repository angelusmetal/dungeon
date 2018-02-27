package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class ORoomGenerator implements RoomGenerator {
	@Override
	public int minWidth() {
		return 9;
	}

	@Override
	public int maxWidth() {
		return 11;
	}

	@Override
	public int minHeight() {
		return 9;
	}

	@Override
	public int maxHeight() {
		return 11;
	}

	@Override
	public Room generate(int left, int bottom, int width, int height) {
		Room room = new Room(left, bottom, width, height);

		// Create a rectangle
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				room.tiles[x][y] = TileType.FLOOR;
			}
		}

		// Cut corners in diamond shape
		int maxCut = Math.min(width, height) / 2 - 1;
		for (int i = maxCut; i >= 0; --i) {
			for (int x = 0; x <= maxCut - i; ++x) {
				room.tiles[x][i] = TileType.VOID;
				room.tiles[x][height - i - 1] = TileType.VOID;
				room.tiles[width - x - 1][i] = TileType.VOID;
				room.tiles[width - x - 1][height - i - 1] = TileType.VOID;
			}
		}

		// Carve the middle
		room.tiles[width / 2 - 1][height / 2 - 1] = TileType.VOID;
		room.tiles[width / 2 - 1][height / 2] = TileType.VOID;
		room.tiles[width / 2 - 1][height / 2 + 1] = TileType.VOID;
		room.tiles[width / 2][height / 2 - 1] = TileType.VOID;
		room.tiles[width / 2][height / 2] = TileType.VOID;
		room.tiles[width / 2][height / 2 + 1] = TileType.VOID;
		room.tiles[width / 2 + 1][height / 2 - 1] = TileType.VOID;
		room.tiles[width / 2 + 1][height / 2] = TileType.VOID;
		room.tiles[width / 2 + 1][height / 2 + 1] = TileType.VOID;

		room.spawnPoints.add(new Vector2(left + 2, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width - 2, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height - 2));

		return room;
	}
}
