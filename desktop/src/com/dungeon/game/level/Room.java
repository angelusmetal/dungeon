package com.dungeon.game.level;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.generator.ModularLevelGenerator;

import java.util.ArrayList;
import java.util.List;

public class Room {
	public final RoomPrototype prototype;
	public final int left;
	public final int bottom;
	public final int width;
	public final int height;
	public final int generation;
	public final List<Vector2> spawnPoints = new ArrayList<>();
	public final List<EntityPlaceholder> placeholders = new ArrayList<>();
	public final List<ModularLevelGenerator.ConnectionPoint> connectionPoints = new ArrayList<>();

	public final Tile[][] tiles;

	public Room(int left, int bottom, int generation, RoomPrototype prototype) {
		this.prototype = prototype;
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
			placeholders.add(placeholder.relativeTo(left, bottom));
		}
		for (ModularLevelGenerator.ConnectionPoint connection : prototype.getConnections()) {
			GridPoint2 newOrigin = connection.origin.cpy().add(left, bottom);
			connectionPoints.add(new ModularLevelGenerator.ConnectionPoint(newOrigin, connection.direction));
		}
	}

	@Override
	public String toString() {
		return "[" + generation + "] left: " + left + ", bottom: " + bottom + ", width: " + width + ", height: " + height + ", connectionPoints: [" + connectionPoints + "]";
	}
}
