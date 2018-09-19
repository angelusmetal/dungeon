package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class TileSheet {
	public final Texture texture;
	public final int tile_size;

	public TileSheet(Texture texture, int tile_size) {
		this.texture = texture;
		this.tile_size = tile_size;
	}

	public TextureRegion getTile(float x, float y) {
		return new TextureRegion(texture, (int) (tile_size * x), (int) (tile_size * y), tile_size, tile_size);
	}

	public TextureRegion getTile(float x, float y, float x_tiles, float y_tiles) {
		return new TextureRegion(texture, (int) (tile_size * x), (int) (tile_size * y), (int) (tile_size * x_tiles), (int) (tile_size * y_tiles));
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
