package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.entity.EntityType;

import javax.xml.ws.Provider;

public class LevelSpec {
	private final Tileset tileset;
	private final Provider<Color> light;
	private final List<RoomPrototype> rooms;
	private final List<EntityType> monsters;

	public LevelSpec(Tileset tileset, Provider<Color> light, List<RoomPrototype> rooms, List<EntityType> monsters) {
		this.tileset = tileset;
		this.light = light;
		this.rooms = rooms;
		this.monsters = monsters;
	}

	public Tileset getTileset() {
		return tileset;
	}

	public Provider<Color> getLight() {
		return light;
	}

	public List<RoomPrototype> getRooms() {
		return rooms;
	}

	public List<EntityType> getMonsters() {
		return monsters;
	}
}
