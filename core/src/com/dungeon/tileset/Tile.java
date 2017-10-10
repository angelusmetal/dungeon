package com.dungeon.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
	public Animation<TextureRegion> animation;

	public Tile(TextureRegion textureRegion) {
		this.animation = new Animation<TextureRegion>(0f, textureRegion);
	}
	public Tile(Animation<TextureRegion> animation) {
		this.animation = animation;
	}
}
