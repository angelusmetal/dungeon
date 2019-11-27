package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class TileSheet {
	public final Texture texture;
	public final int tile_width;
	public final int tile_height;

	public TileSheet(Texture texture, int tile_size) {
		this.texture = texture;
		this.tile_width = tile_size;
		this.tile_height = tile_size;
	}

	public TileSheet(Texture texture, int tile_width, int tile_height) {
		this.texture = texture;
		this.tile_width = tile_width;
		this.tile_height = tile_height;
	}

	public TextureRegion getTile(float x, float y) {
		return new TextureRegion(texture, (int) (tile_width * x), (int) (tile_height * y), tile_width, tile_height);
	}

	public TextureRegion getTile(float x, float y, float x_tiles, float y_tiles) {
		return new TextureRegion(texture, (int) (tile_width * x), (int) (tile_height * y), (int) (tile_width * x_tiles), (int) (tile_height * y_tiles));
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
