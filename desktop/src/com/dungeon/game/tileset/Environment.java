package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.game.level.RoomPrototype;

import java.util.List;
import java.util.function.Supplier;

public class Environment {
	private final int tilesize;
	private final Tileset tileset;
	private final Tileset wallTileset;
	private final Supplier<Color> light;
	private final List<RoomPrototype> rooms;
	private final List<String> monsters;

	public Environment(Tileset tileset, Tileset wallTileset, Supplier<Color> light, List<RoomPrototype> rooms, List<String> monsters) {
		this.tilesize = 48; // FIXME make this configurable
		this.tileset = tileset;
		this.wallTileset = wallTileset;
		this.light = light;
		this.rooms = rooms;
		this.monsters = monsters;
	}

	public int getTilesize() {
		return tilesize;
	}

	public Tileset getTileset() {
		return tileset;
	}

	public Tileset getWallTileset() {
		return wallTileset;
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
