package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class Room {
	public final int left;
	public final int bottom;
	public final int width;
	public final int height;
	public final int generation;
	public final List<Vector2> spawnPoints = new ArrayList<>();
	public final List<EntityPlaceholder> placeholders = new ArrayList<>();
	public final List<ProceduralLevelGenerator.ConnectionPoint> connectionPoints = new ArrayList<>();

	public final TileType[][] tiles;

	public Room(int left, int bottom, int width, int height, int generation) {
		tiles = new TileType[width][height];
		this.left = left;
		this.bottom = bottom;
		this.width = width;
		this.height = height;
		this.generation = generation;
	}

	public Room(int left, int bottom, int generation, RoomPrototype prototype) {
		this.tiles = prototype.getTiles();
		this.left = left;
		this.bottom = bottom;
		this.width = prototype.getTiles().length;
		this.height = prototype.getTiles()[0].length;
		this.generation = generation;
		for (Vector2 spawn : prototype.getSpawnPoints()) {
			spawnPoints.add(new Vector2(spawn.x + left, spawn.y + bottom));
		}
		for (EntityPlaceholder placeholder : prototype.getPlaceholders()) {
			placeholders.add(new EntityPlaceholder(placeholder.getType(), new Vector2(placeholder.getOrigin().x + left, placeholder.getOrigin().y + bottom), placeholder.getChance()));
		}
		for (ProceduralLevelGenerator.ConnectionPoint connection : prototype.getConnections()) {
			connectionPoints.add(new ProceduralLevelGenerator.ConnectionPoint(connection.coords.x + left, connection.coords.y + bottom, connection.direction));
		}
	}

	@Override
	public String toString() {
		return "[" + generation + "] left: " + left + ", bottom: " + bottom + ", width: " + width + ", height: " + height + ", connectionPoints: [" + connectionPoints + "]";
	}
}
