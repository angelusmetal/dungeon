package com.dungeon.game.level.room;

import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class RectangleRoomGenerator implements RoomGenerator {
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
		Room room = new Room(left, bottom, width, height);
		room.topLeft.x = left;
		room.topLeft.y = bottom + height;
		room.bottomRight.x = left + width;
		room.bottomRight.y = bottom;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				room.tiles[x][y] = TileType.FLOOR;
			}
		}
		return room;
	}
}
