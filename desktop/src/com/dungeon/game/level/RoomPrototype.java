package com.dungeon.game.level;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.generator.ModularLevelGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class RoomPrototype {
	private final String name;
	private final Tile[][] tiles;
	private final List<ModularLevelGenerator.ConnectionPoint> connections;
	private final List<Vector2> spawnPoints;
	private final List<EntityPlaceholder> placeholders;
	private final int maxOccurrences;
	private final int minDepth;

	private RoomPrototype(Builder builder) {
		name = builder.name;
		tiles = builder.tiles;
		connections = builder.connections.stream().map(this::point).collect(Collectors.toList());;
		spawnPoints = builder.spawnPoints;
		placeholders = builder.placeholders;
		maxOccurrences = builder.maxOccurrences;
		minDepth = builder.minDepth;
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
		} else if (!tiles[origin.x - 1][origin.y].isSolid()) {
			direction = ModularLevelGenerator.Direction.RIGHT;
		} else if (!tiles[origin.x + 1][origin.y].isSolid()) {
			direction = ModularLevelGenerator.Direction.LEFT;
		} else if (!tiles[origin.x][origin.y - 1].isSolid()) {
			direction = ModularLevelGenerator.Direction.UP;
		} else if (!tiles[origin.x][origin.y + 1].isSolid()) {
			direction = ModularLevelGenerator.Direction.DOWN;
		} else {
			throw new IllegalArgumentException("Invalid connection point coordinates: " + origin);
		}
		return new ModularLevelGenerator.ConnectionPoint(origin, direction);
	}

	public String getName() {
		return name;
	}

	public Tile[][] getTiles() {
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

	public int getMinDepth() {
		return minDepth;
	}

	public static final class Builder {
		private String name;
		private Tile[][] tiles;
		private List<GridPoint2> connections;
		private List<Vector2> spawnPoints;
		private List<EntityPlaceholder> placeholders;
		private int maxOccurrences;
		private int minDepth;

//		public Builder() { }
//
//		public Builder(RoomPrototype copy) {
//			this.name = copy.getName();
//			this.tiles = copy.getTiles();
//			this.connections = copy.getConnections();
//			this.spawnPoints = copy.getSpawnPoints();
//			this.placeholders = copy.getPlaceholders();
//			this.maxOccurrences = copy.getMaxOccurrences();
//		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder tiles(Tile[][] tiles) {
			this.tiles = tiles;
			return this;
		}

		public Builder connections(List<GridPoint2> connections) {
			this.connections = connections;
			return this;
		}

		public Builder spawnPoints(List<Vector2> spawnPoints) {
			this.spawnPoints = spawnPoints;
			return this;
		}

		public Builder placeholders(List<EntityPlaceholder> placeholders) {
			this.placeholders = placeholders;
			return this;
		}

		public Builder maxOccurrences(int maxOccurrences) {
			this.maxOccurrences = maxOccurrences;
			return this;
		}

		public Builder minDepth(int minDepth) {
			this.minDepth = minDepth;
			return this;
		}

		public RoomPrototype build() {
			return new RoomPrototype(this);
		}
	}
}
