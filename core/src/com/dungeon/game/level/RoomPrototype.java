package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.moandjiezana.toml.Toml;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RoomPrototype {
	private final TileType[][] tiles;
	private final List<ProceduralLevelGenerator.ConnectionPoint> connections;
	private final List<Vector2> spawnPoints;
	private final List<EntityPlaceholder> placeholders;

	public RoomPrototype(TileType[][] tiles, List<ProceduralLevelGenerator.ConnectionPoint> connections, List<Vector2> spawnPoints, List<EntityPlaceholder> placeholders) {
		this.tiles = tiles;
		this.connections = connections;
		this.spawnPoints = spawnPoints;
		this.placeholders = placeholders;
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

	public static RoomPrototype fromToml(Toml toml) {
		return new RoomPrototype(getTiles(toml), getConnections(toml), getSpawns(toml), getPlaceholders(toml));
	}

	private static TileType[][] getTiles(Toml toml) {
		List<List<String>> tiles = toml.getList("tiles");
		if (tiles.size() == 0) {
			throw new RuntimeException("'tiles' cannot be empty");
		}
		int height = tiles.size();
		int width = tiles.get(0).size();
		for (int i = 0; i < tiles.size(); ++i) {
			if (tiles.get(i).size() != width) {
				throw new RuntimeException("all 'tiles' rows must have the same amount of elements (" + width + "), but row " + i + " has " + (tiles.get(i).size() + " instead"));
			}
		}
		// Invert coordinates
		TileType[][] array = new TileType[width][];
		for (int x = 0; x < width; ++x) {
			array[x] = new TileType[height];
			for (int y = 0; y < height; ++y) {
				array[x][height - y - 1] = TileType.valueOf(tiles.get(y).get(x));
			}
		}
		return array;
	}

	private static List<ProceduralLevelGenerator.ConnectionPoint> getConnections(Toml toml) {
		return toml.<HashMap>getList("connections").stream().map(t -> {
			Number x = (Number) t.get("x");
			Number y = (Number) t.get("y");
			String type = (String) t.get("type");
			if (x == null) {
				throw new RuntimeException("'x' cannot be null in room connection definition: " + t);
			}
			if (y == null) {
				throw new RuntimeException("'y' cannot be null in room connection definition: " + t);
			}
			if (type == null) {
				throw new RuntimeException("'type' cannot be null in room connection definition: " + t);
			}
			ProceduralLevelGenerator.Direction direction = ProceduralLevelGenerator.Direction.valueOf(type);
			return new ProceduralLevelGenerator.ConnectionPoint(x.intValue(), y.intValue(), direction);
		}).collect(Collectors.toList());
	}

	private static List<Vector2> getSpawns(Toml toml) {
		return toml.<HashMap>getList("spawns").stream().map(t -> {
			Number x = (Number) t.get("x");
			Number y = (Number) t.get("y");
			if (x == null) {
				throw new RuntimeException("'x' cannot be null in room spawn definition: " + t);
			}
			if (y == null) {
				throw new RuntimeException("'y' cannot be null in room spawn definition: " + t);
			}
			return new Vector2(x.floatValue(), y.floatValue());
		}).collect(Collectors.toList());
	}

	private static List<EntityPlaceholder> getPlaceholders(Toml toml) {
		return toml.<HashMap>getList("placeholders").stream().map(t -> {
			Number x = (Number) t.get("x");
			Number y = (Number) t.get("y");
			Number chance = (Number) t.get("chance");
			String type = (String) t.get("type");
			if (x == null) {
				throw new RuntimeException("'x' cannot be null in room placeholder definition: " + t);
			}
			if (y == null) {
				throw new RuntimeException("'y' cannot be null in room placeholder definition: " + t);
			}
			if (type == null) {
				throw new RuntimeException("'type' cannot be null in room placeholder definition: " + t);
			}
			float ch = chance == null ? 1 : chance.floatValue();
			return new EntityPlaceholder(EntityType.valueOf(type), new Vector2(x.floatValue(), y.floatValue()), ch);
		}).collect(Collectors.toList());
	}
}
