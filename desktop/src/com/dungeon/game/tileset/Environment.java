package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.game.level.RoomPrototype;

import java.util.List;
import java.util.function.Supplier;

public class Environment {
	private final Tileset tileset;
	private final Supplier<Color> light;
	private final List<RoomPrototype> rooms;
	private final List<String> monsters;

	public Environment(Tileset tileset, Supplier<Color> light, List<RoomPrototype> rooms, List<String> monsters) {
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

	public List<String> getMonsters() {
		return monsters;
	}
}
