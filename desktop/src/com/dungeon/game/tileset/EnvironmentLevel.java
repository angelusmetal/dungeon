package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TilePrototype;

import java.util.List;
import java.util.function.Supplier;

public class EnvironmentLevel {
	private final int tilesize;
	private final TilePrototype fillTile;
	private final TilePrototype voidTile;
	private final Supplier<Color> light;
	private final List<RoomPrototype> rooms;
	private final List<String> monsters;
	private final int tier;
	private final String music;
	private final String title;
	private final String subtitle;

	public EnvironmentLevel(int tilesize, TilePrototype fillTile, TilePrototype voidTile, Supplier<Color> light, List<RoomPrototype> rooms, List<String> monsters, int tier, String music, String title, String subtitle) {
		this.tilesize = tilesize;
		this.fillTile = fillTile;
		this.voidTile = voidTile;
		this.light = light;
		this.rooms = rooms;
		this.monsters = monsters;
		this.tier = tier;
		this.music = music;
		this.title = title;
		this.subtitle = subtitle;
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

	public int getTier() {
		return tier;
	}

	public String getMusic() {
		return music;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}
}
