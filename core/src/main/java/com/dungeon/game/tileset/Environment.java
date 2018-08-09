package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.entity.EntityType;

import java.util.function.Supplier;
import java.util.List;

public class Environment {
	private final Tileset tileset;
	private final Supplier<Color> light;
	private final List<RoomPrototype> rooms;
	private final List<EntityType> monsters;

	public Environment(Tileset tileset, Supplier<Color> light, List<RoomPrototype> rooms, List<EntityType> monsters) {
		this.tileset = tileset;
		this.light = light;
		this.rooms = rooms;
		this.monsters = monsters;
	}

	public Tileset getTileset() {
		return tileset;
	}

	public Supplier<Color> getLight() {
		return light;
	}

	public List<RoomPrototype> getRooms() {
		return rooms;
	}

	public List<EntityType> getMonsters() {
		return monsters;
	}
}
