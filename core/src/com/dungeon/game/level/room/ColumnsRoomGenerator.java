package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class ColumnsRoomGenerator implements RoomGenerator {
	@Override
	public int minWidth() {
		return 9;
	}

	@Override
	public int maxWidth() {
		return 13;
	}

	@Override
	public int minHeight() {
		return 9;
	}

	@Override
	public int maxHeight() {
		return 13;
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

		// Add columns
		room.tiles[1][1] = TileType.VOID;
		room.tiles[1][2] = TileType.VOID;
		room.tiles[2][1] = TileType.VOID;
		room.tiles[2][2] = TileType.VOID;

		room.tiles[1][height - 2] = TileType.VOID;
		room.tiles[1][height - 3] = TileType.VOID;
		room.tiles[2][height - 2] = TileType.VOID;
		room.tiles[2][height - 3] = TileType.VOID;

		room.tiles[width - 2][1] = TileType.VOID;
		room.tiles[width - 2][2] = TileType.VOID;
		room.tiles[width - 3][1] = TileType.VOID;
		room.tiles[width - 3][2] = TileType.VOID;

		room.tiles[width - 2][height - 2] = TileType.VOID;
		room.tiles[width - 2][height - 3] = TileType.VOID;
		room.tiles[width - 3][height - 2] = TileType.VOID;
		room.tiles[width - 3][height - 3] = TileType.VOID;

		// Add spawn points
		room.spawnPoints.add(new Vector2(left + width / 2 - 1, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2 - 1));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2 + 1));
		room.spawnPoints.add(new Vector2(left + width / 2 + 1, bottom + height / 2));

		return room;
	}
}
