package com.dungeon.game.level;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.generator.ModularLevelGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class RoomPrototype {
	private final String name;
	private final TileType[][] tiles;
	private final List<ModularLevelGenerator.ConnectionPoint> connections;
	private final List<Vector2> spawnPoints;
	private final List<EntityPlaceholder> placeholders;
	private final int maxOccurrences;

	public RoomPrototype(String name, TileType[][] tiles, List<GridPoint2> connections, List<Vector2> spawnPoints, List<EntityPlaceholder> placeholders, int maxOccurrences) {
		this.name = name;
		this.tiles = tiles;
		this.connections = connections.stream().map(this::point).collect(Collectors.toList());
		this.spawnPoints = spawnPoints;
		this.placeholders = placeholders;
		this.maxOccurrences = maxOccurrences;
	}

	private ModularLevelGenerator.ConnectionPoint point(GridPoint2 origin) {
		int width = tiles.length;
		int height = tiles[0].length;
		if (origin.x < 0 || origin.y < 0 || origin.x >= width || origin.y >= height) {
			throw new IllegalArgumentException("Invalid connection point coordinates: " + origin);
		}
		ModularLevelGenerator.Direction direction;
		if (origin.x == 0) {
			direction = ModularLevelGenerator.Direction.LEFT;
		} else if (origin.x == width - 1) {
			direction = ModularLevelGenerator.Direction.RIGHT;
		} else if (origin.y == 0) {
			direction = ModularLevelGenerator.Direction.DOWN;
		} else if (origin.y == height -1) {
			direction = ModularLevelGenerator.Direction.UP;
		} else if (tiles[origin.x - 1][origin.y] == TileType.FLOOR) {
			direction = ModularLevelGenerator.Direction.RIGHT;
		} else if (tiles[origin.x + 1][origin.y] == TileType.FLOOR) {
			direction = ModularLevelGenerator.Direction.LEFT;
		} else if (tiles[origin.x][origin.y - 1] == TileType.FLOOR) {
			direction = ModularLevelGenerator.Direction.UP;
		} else if (tiles[origin.x][origin.y + 1] == TileType.FLOOR) {
			direction = ModularLevelGenerator.Direction.DOWN;
		} else {
			throw new IllegalArgumentException("Invalid connection point coordinates: " + origin);
		}
		return new ModularLevelGenerator.ConnectionPoint(origin, direction);
	}

	public String getName() {
		return name;
	}

	public TileType[][] getTiles() {
		return tiles;
	}

	public List<ModularLevelGenerator.ConnectionPoint> getConnections() {
		return connections;
	}

	public List<Vector2> getSpawnPoints() {
		return spawnPoints;
	}

	public List<EntityPlaceholder> getPlaceholders() {
		return placeholders;
	}

	public int getMaxOccurrences() {
		return maxOccurrences;
	}
}
