package com.dungeon.game.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.dungeon.engine.physics.LevelTiles;
import com.dungeon.engine.render.Material;
import com.dungeon.game.Game;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.resource.DungeonResources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level implements LevelTiles {
	private final int width;
	private final int height;
	private final Tile[] tiles;
	private final List<EntityPlaceholder> entityPlaceholders = new ArrayList<>();
	private List<Room> rooms = Collections.emptyList();

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width * height];
		TilePrototype solid = DungeonResources.tiles.get("solid");
		for (int i = 0; i < tiles.length; ++i) {
			tiles[i] = new Tile(solid);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setFloorAnimation(int x, int y, Animation<Material> animation) {
		getTile(x, y).floorAnimation = animation;
	}

	public Animation<Material> getFloorAnimation(int x, int y) {
		return getTile(x, y).floorAnimation;
	}

	public void setWallAnimation(int x, int y, Animation<Material> animation) {
		getTile(x, y).wallAnimation = animation;
	}

	public Animation<Material> getWallAnimation(int x, int y) {
		return getTile(x, y).wallAnimation;
	}

	@Override
	public boolean isSolid(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return true;
		}
		return getTile(x, y).isSolid();
	}

	public boolean isDiscovered(int x, int y) {
		return getTile(x, y).discovered;
	}

	public void setDiscovered(int x, int y) {
		getTile(x, y).discovered = true;
	}

	@Override
	public int getTileSize() {
		return Game.getEnvironment().getTilesize();
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
//		try {
			return tiles[x * height + y];
//		} catch (ArrayIndexOutOfBoundsException e) {
//			throw new ArrayIndexOutOfBoundsException("Index array out of range: " + (x * height + y) + " (" + x + ", " + y + ") - level size: [" + width + ", " + height + "]");
//		}
	}

	public void setTilePrototype(int x, int y, TilePrototype prototype) {
		tiles[x * height + y].setPrototype(prototype);
	}
}
