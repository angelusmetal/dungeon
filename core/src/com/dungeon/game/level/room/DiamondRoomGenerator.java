package com.dungeon.game.level.room;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.TileType;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;

public class DiamondRoomGenerator implements RoomGenerator {
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
				room.tiles[x][y] = TileType.VOID;
			}
		}
		for (int x = 1; x < width - 1; ++x) {
			for (int y = 1; y < height - 1; ++y) {
				room.tiles[x][y] = TileType.FLOOR;
			}
		}

		// Cut corners in diamond shape
		int maxCut = Math.min(width, height) / 2 - 1;
		for (int i = maxCut; i >= 1; --i) {
			for (int x = 1; x <= maxCut - i; ++x) {
				room.tiles[x][i] = TileType.VOID;
				room.tiles[x][height - i - 1] = TileType.VOID;
				room.tiles[width - x - 1][i] = TileType.VOID;
				room.tiles[width - x - 1][height - i - 1] = TileType.VOID;
			}
		}

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

		return room;
	}
}
