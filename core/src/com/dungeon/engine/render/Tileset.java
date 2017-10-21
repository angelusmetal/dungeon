package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Tileset {
	public final Texture texture;
	public final int tile_width;
	public final int tile_height;

	public Tileset(Texture texture, int tile_size) {
		this.texture = texture;
		this.tile_height = tile_size;
		this.tile_width = tile_size;
	}

	public Tileset(Texture texture, int tile_width, int tile_height) {
		this.texture = texture;
		this.tile_width = tile_width;
		this.tile_height = tile_height;
	}

	public TextureRegion getTile(int x, int y) {
		return new TextureRegion(texture, tile_width * x, tile_height * y, tile_width, tile_height);
	}

	public TextureRegion getTile(int x, int y, int x_tiles, int y_tiles) {
		return new TextureRegion(texture, tile_width * x, tile_height * y, tile_width * x_tiles, tile_height * y_tiles);
	}

	public Animation<TextureRegion> loop(float frameDuration, TextureRegion... frames) {
		Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation;
	}

	public List<Tile> getAllTiles() throws IllegalAccessException {
		List<Tile> tiles = new ArrayList<Tile>();
		for (Field field : this.getClass().getFields()) {
			if (field.getType().equals(Tile.class)) {
				tiles.add((Tile) field.get(this));
			}
		}
		return tiles;
	}

	public void dispose() {
		// TODO Make sure we´re the owners of this to dispose it
		texture.dispose();
	}
}
