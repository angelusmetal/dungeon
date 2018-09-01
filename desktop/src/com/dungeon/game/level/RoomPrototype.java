package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.List;

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

}
