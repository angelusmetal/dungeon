package com.dungeon.game.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.physics.LevelTiles;
import com.dungeon.game.Game;
import com.dungeon.game.level.entity.EntityPlaceholder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level implements LevelTiles {
	private final int width;
	private final int height;
	private final Tile[] map;
	private final List<EntityPlaceholder> entityPlaceholders = new ArrayList<>();
	private List<Room> rooms = Collections.emptyList();

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		this.map = new Tile[width * height];
		for (int i = 0; i < map.length; ++i) {
			map[i] = new Tile();
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setAnimation(int x, int y, Animation<TextureRegion> animation) {
		getTile(x, y).animation = animation;
	}

	public Animation<TextureRegion> getAnimation(int x, int y) {
		return getTile(x, y).animation;
	}

	public void setSolid(int x, int y, boolean solid) {
		getTile(x, y).solid = solid;
	}

	@Override
	public boolean isSolid(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return true;
		}
		return getTile(x, y).solid;
	}

	@Override
	public int getTileSize() {
		return Game.getEnvironment().getTileset().tile_size;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public List<EntityPlaceholder> getEntityPlaceholders() {
		return entityPlaceholders;
	}

	private Tile getTile(int x, int y) {
		return map[x * height + y];
	}

	private static class Tile {
		Animation<TextureRegion> animation;
		boolean solid = false;
	}
}
