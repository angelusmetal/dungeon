package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;

public class CornersRoomGenerator implements RoomGenerator {

	private static TileType[] DECORATIONS = {TileType.WALL_DECORATION_1, TileType.WALL_DECORATION_2, TileType.WALL_DECORATION_3, TileType.WALL_DECORATION_4};

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
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width / 2 - 0.5f, bottom + height - 0.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width / 2 + 1.5f, bottom + height - 0.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + 1, bottom + height / 2 + 1.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + 1, bottom + height / 2 - 0.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width - 1, bottom + height / 2 + 1.5f));
		room.placeholders.add(EntityPlaceholder.of(EntityType.TORCH, left + width - 1, bottom + height / 2 - 0.5f));

		// Add decorations
		if (width > 7 && Rand.chance(0.2f)) {
			TileType decoration = Rand.pick(DECORATIONS);
			room.tiles[2][height - 1] = decoration;
			room.tiles[width - 3][height - 1] = decoration;
		}

		return room;
	}
}
