package com.dungeon.game.level.room;

import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class CornersRoomGenerator implements RoomGenerator {
	@Override
	public int minWidth() {
		return 5;
	}

	@Override
	public int maxWidth() {
		return 20;
	}

	@Override
	public int minHeight() {
		return 5;
	}

	@Override
	public int maxHeight() {
		return 20;
	}

	@Override
	public Room generate(int left, int bottom, int width, int height) {
		Room room = new Room(left, bottom, width, height, left + width / 2, bottom + height / 2);

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
		return room;
	}
}
