package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class CornersRoomGenerator implements RoomGenerator {
	@Override
	public int minWidth() {
		return 5;
	}

	@Override
	public int maxWidth() {
		return 9;
	}

	@Override
	public int minHeight() {
		return 5;
	}

	@Override
	public int maxHeight() {
		return 9;
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

		// But chop away the corners...
		room.tiles[0][0] = TileType.VOID;
		room.tiles[0][height - 1] = TileType.VOID;
		room.tiles[width - 1][0] = TileType.VOID;
		room.tiles[width - 1][height - 1] = TileType.VOID;

		// Add spawn points
		room.spawnPoints.add(new Vector2(left + width / 2 - 1, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2 - 1));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2 + 1));
		room.spawnPoints.add(new Vector2(left + width / 2 + 1, bottom + height / 2));

		return room;
	}
}
