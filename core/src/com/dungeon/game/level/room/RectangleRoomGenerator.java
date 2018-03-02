package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class RectangleRoomGenerator implements RoomGenerator {
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

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				room.tiles[x][y] = TileType.FLOOR;
			}
		}

		// Add spawn points
		room.spawnPoints.add(new Vector2(left + 1, bottom + 1));
		room.spawnPoints.add(new Vector2(left + 1, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + 1, bottom + height - 1));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + 1));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width / 2, bottom + height - 1));
		room.spawnPoints.add(new Vector2(left + width - 1, bottom + 1));
		room.spawnPoints.add(new Vector2(left + width - 1, bottom + height / 2));
		room.spawnPoints.add(new Vector2(left + width - 1, bottom + height - 1));

		// Add torches
		room.torches.add(new Vector2(left + width / 2 - 0.5f, bottom + height + 0.5f));
		room.torches.add(new Vector2(left + width / 2 + 1.5f, bottom + height + 0.5f));
		room.torches.add(new Vector2(left, bottom + height / 2 + 1.5f));
		room.torches.add(new Vector2(left, bottom + height / 2 - 0.5f));
		room.torches.add(new Vector2(left + width, bottom + height / 2 + 1.5f));
		room.torches.add(new Vector2(left + width, bottom + height / 2 - 0.5f));

		return room;
	}
}
