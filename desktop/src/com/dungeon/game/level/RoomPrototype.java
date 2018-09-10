package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.List;
import java.util.stream.Collectors;

public class RoomPrototype {
	private final TileType[][] tiles;
	private final List<ProceduralLevelGenerator.ConnectionPoint> connections;
	private final List<Vector2> spawnPoints;
	private final List<EntityPlaceholder> placeholders;

	public RoomPrototype(TileType[][] tiles, List<Vector2> connections, List<Vector2> spawnPoints, List<EntityPlaceholder> placeholders) {
		this.tiles = tiles;
		this.connections = connections.stream().map(this::point).collect(Collectors.toList());
		this.spawnPoints = spawnPoints;
		this.placeholders = placeholders;
	}

	private ProceduralLevelGenerator.ConnectionPoint point(Vector2 coords) {
		int x = (int) coords.x;
		int y = (int) coords.y;
		int width = tiles.length;
		int height = tiles[0].length;
		if (x < 0 || y < 0 || x >= width || y >= height) {
			throw new IllegalArgumentException("Invalid connection point coordinates: " + coords);
		}
		ProceduralLevelGenerator.Direction direction;
		if (x == 0) {
			direction = ProceduralLevelGenerator.Direction.LEFT;
		} else if (x == width - 1) {
			direction = ProceduralLevelGenerator.Direction.RIGHT;
		} else if (y == 0) {
			direction = ProceduralLevelGenerator.Direction.DOWN;
		} else if (y == height -1) {
			direction = ProceduralLevelGenerator.Direction.UP;
		} else if (tiles[x - 1][y] == TileType.FLOOR) {
			direction = ProceduralLevelGenerator.Direction.RIGHT;
		} else if (tiles[x + 1][y] == TileType.FLOOR) {
			direction = ProceduralLevelGenerator.Direction.LEFT;
		} else if (tiles[x][y - 1] == TileType.FLOOR) {
			direction = ProceduralLevelGenerator.Direction.UP;
		} else if (tiles[x][y + 1] == TileType.FLOOR) {
			direction = ProceduralLevelGenerator.Direction.DOWN;
		} else {
			throw new IllegalArgumentException("Invalid connection point coordinates: " + coords);
		}
		return new ProceduralLevelGenerator.ConnectionPoint(x, y, direction);
	}

	public TileType[][] getTiles() {
		return tiles;
	}

	public List<ProceduralLevelGenerator.ConnectionPoint> getConnections() {
		return connections;
	}

	public List<Vector2> getSpawnPoints() {
		return spawnPoints;
	}

	public List<EntityPlaceholder> getPlaceholders() {
		return placeholders;
	}

}
