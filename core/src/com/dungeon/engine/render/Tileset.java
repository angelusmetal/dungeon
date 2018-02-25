package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Tileset {
	public final Texture texture;
	public final int tile_size;
	private static final Random random = new Random();

	public Tileset(Texture texture, int tile_size) {
		this.texture = texture;
		this.tile_size = tile_size;
	}

	public TextureRegion getTile(int x, int y) {
		return new TextureRegion(texture, tile_size * x, tile_size * y, tile_size, tile_size);
	}

	public TextureRegion getTile(int x, int y, int x_tiles, int y_tiles) {
		return new TextureRegion(texture, tile_size * x, tile_size * y, tile_size * x_tiles, tile_size * y_tiles);
	}

	protected Tile[] createTiles(TextureRegion ... regions) {
		Tile[] tiles = new Tile[regions.length];
		for (int i = 0; i < regions.length; ++i) {
			tiles[i] = new Tile(regions[i]);
		}
		return tiles;
	}

	protected Tile getRandomTile(Tile[] tiles) {
		return tiles[random.nextInt(tiles.length)];
	}

	public Animation<TextureRegion> loop(float frameDuration, TextureRegion... frames) {
		Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
		animation.setPlayMode(Animation.PlayMode.LOOP);
		return animation;
	}

	public void dispose() {
		// TODO Make sure we´re the owners of this to dispose it
		texture.dispose();
	}
}
