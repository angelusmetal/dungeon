package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;

public class CornersRoomGenerator implements RoomGenerator {

	private static TileType[] DECORATIONS = {TileType.WALL_DECORATION_1, TileType.EXIT.WALL_DECORATION_2, TileType.WALL_DECORATION_3, TileType.WALL_DECORATION_4};

	@Override
	public int minWidth() {
		return 7;
	}

	@Override
	public int maxWidth() {
		return 11;
	}

	@Override
	public int minHeight() {
		return 7;
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
				room.tiles[x][y] = TileType.VOID;
			}
		}
		for (int x = 1; x < width - 1; ++x) {
			for (int y = 1; y < height - 1; ++y) {
				room.tiles[x][y] = TileType.FLOOR;
			}
		}

		// But chop away the corners...
		room.tiles[1][1] = TileType.VOID;
		room.tiles[1][height - 2] = TileType.VOID;
		room.tiles[width - 2][1] = TileType.VOID;
		room.tiles[width - 2][height - 2] = TileType.VOID;

		// Add spawn points
		room.spawnPoints.add(new Vector2(left + 2, bottom + height / 2f));
		room.spawnPoints.add(new Vector2(left + width / 2f, bottom + 2));
		room.spawnPoints.add(new Vector2(left + width / 2f, bottom + height / 2f));
		room.spawnPoints.add(new Vector2(left + width / 2f, bottom + height - 2));
		room.spawnPoints.add(new Vector2(left + width - 2, bottom + height / 2f));

		// Add torches
		room.torches.add(new Vector2(left + width / 2 - 0.5f, bottom + height - 0.5f));
		room.torches.add(new Vector2(left + width / 2 + 1.5f, bottom + height - 0.5f));
		room.torches.add(new Vector2(left + 1, bottom + height / 2 + 1.5f));
		room.torches.add(new Vector2(left + 1, bottom + height / 2 - 0.5f));
		room.torches.add(new Vector2(left + width - 1, bottom + height / 2 + 1.5f));
		room.torches.add(new Vector2(left + width - 1, bottom + height / 2 - 0.5f));

		// Add decorations
		if (width > 7 && Math.random() > 0.2d) {
			TileType decoration = DECORATIONS[(int) (Math.random() * DECORATIONS.length)];
			room.tiles[2][height - 1] = decoration;
			room.tiles[width - 3][height - 1] = decoration;
		}

		return room;
	}
}
