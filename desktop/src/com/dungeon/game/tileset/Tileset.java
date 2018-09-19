package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;

public abstract class Tileset extends TileSheet {

	public Tileset(Texture texture, int tile_size) {
		super(texture, tile_size);
	}

	public abstract Animation<TextureRegion> out();
	public abstract Animation<TextureRegion> floor();
	public abstract Animation<TextureRegion> convexLowerLeft();
	public abstract Animation<TextureRegion> convexLowerRight();
	public abstract Animation<TextureRegion> convexUpperLeft();
	public abstract Animation<TextureRegion> convexUpperRight();
	public abstract Animation<TextureRegion> concaveLowerLeft();
	public abstract Animation<TextureRegion> concaveLowerRight();
	public abstract Animation<TextureRegion> concaveUpperLeft();
	public abstract Animation<TextureRegion> concaveUpperRight();
	public abstract Animation<TextureRegion> concaveLower();
	public abstract Animation<TextureRegion> concaveUpper();
	public abstract Animation<TextureRegion> concaveLeft();
	public abstract Animation<TextureRegion> concaveRight();
	public abstract Animation<TextureRegion> wallDecoration1();
	public abstract Animation<TextureRegion> wallDecoration2();
	public abstract Animation<TextureRegion> wallDecoration3();
	public abstract Animation<TextureRegion> wallDecoration4();
}
