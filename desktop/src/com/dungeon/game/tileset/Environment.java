package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TilePrototype;

import java.util.List;
import java.util.function.Supplier;

public class Environment {
	private final int tilesize;
	private final TilePrototype fillTile;
	private final TilePrototype voidTile;
	private final Supplier<Color> light;
	private final List<RoomPrototype> rooms;
	private final List<String> monsters;

	public Environment(int tilesize, TilePrototype fillTile, TilePrototype voidTile, Supplier<Color> light, List<RoomPrototype> rooms, List<String> monsters) {
		this.tilesize = tilesize;
		this.fillTile = fillTile;
		this.voidTile = voidTile;
		this.light = light;
		this.rooms = rooms;
		this.monsters = monsters;
	}

	public int getTilesize() {
		return tilesize;
	}

	public TilePrototype getFillTile() {
		return fillTile;
	}

	public TilePrototype getVoidTile() {
		return voidTile;
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
