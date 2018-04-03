package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;

public class ColumnsRoomGenerator implements RoomGenerator {

	private static TileType[] DECORATIONS = {TileType.WALL_DECORATION_1, TileType.EXIT.WALL_DECORATION_2, TileType.WALL_DECORATION_3, TileType.WALL_DECORATION_4};

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
	public Room generate(int left, int bottom, int width, int height, int generation) {
		Room room = new Room(left, bottom, width, height, generation);

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

		// Add columns
		room.tiles[2][2] = TileType.VOID;
		room.tiles[2][3] = TileType.VOID;
		room.tiles[3][2] = TileType.VOID;
		room.tiles[3][3] = TileType.VOID;

		room.tiles[2][height - 3] = TileType.VOID;
		room.tiles[2][height - 4] = TileType.VOID;
		room.tiles[3][height - 3] = TileType.VOID;
		room.tiles[3][height - 4] = TileType.VOID;

		room.tiles[width - 3][2] = TileType.VOID;
		room.tiles[width - 3][3] = TileType.VOID;
		room.tiles[width - 4][2] = TileType.VOID;
		room.tiles[width - 4][3] = TileType.VOID;

		room.tiles[width - 3][height - 3] = TileType.VOID;
		room.tiles[width - 3][height - 4] = TileType.VOID;
		room.tiles[width - 4][height - 3] = TileType.VOID;
		room.tiles[width - 4][height - 4] = TileType.VOID;

		// Add spawn points
		room.spawnPoints.add(new Vector2(left + 2, bottom + height / 2f));
		room.spawnPoints.add(new Vector2(left + width / 2f, bottom + 2));
		room.spawnPoints.add(new Vector2(left + width / 2f, bottom + height / 2f));
		room.spawnPoints.add(new Vector2(left + width / 2f, bottom + height - 2));
		room.spawnPoints.add(new Vector2(left + width - 2, bottom + height / 2f));

		// Add torches
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width / 2 - 0.5f, bottom + height - 0.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width / 2 + 1.5f, bottom + height - 0.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + 1, bottom + height / 2 + 1.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + 1, bottom + height / 2 - 0.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width - 1, bottom + height / 2 + 1.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width - 1, bottom + height / 2 - 0.5f));

		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + 3f, bottom + height - 3.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width - 3f, bottom + height - 3.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + 3f, bottom + 2.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width - 3f, bottom + 2.5f));

		// Add decorations
		if (width > 7 && Math.random() > 0.2d) {
			TileType decoration = DECORATIONS[(int) (Math.random() * DECORATIONS.length)];
			room.tiles[2][height - 1] = decoration;
			room.tiles[width - 3][height - 1] = decoration;
		}

		return room;
	}
}
