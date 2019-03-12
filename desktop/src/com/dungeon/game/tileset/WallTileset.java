package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;

public abstract class WallTileset extends TileSheet {

	public WallTileset(Texture texture, int tile_size) {
		super(texture, tile_size);
	}

	public abstract Animation<TextureRegion> down();
	public abstract Animation<TextureRegion> left();
	public abstract Animation<TextureRegion> up();
	public abstract Animation<TextureRegion> right();
	public abstract Animation<TextureRegion> downRight();
	public abstract Animation<TextureRegion> downLeft();
	public abstract Animation<TextureRegion> upLeft();
	public abstract Animation<TextureRegion> upRight();
	public abstract Animation<TextureRegion> upDown();
	public abstract Animation<TextureRegion> leftRight();
	public abstract Animation<TextureRegion> downLeftRight();
	public abstract Animation<TextureRegion> upDownLeft();
	public abstract Animation<TextureRegion> upLeftRight();
	public abstract Animation<TextureRegion> upDownRight();
	public abstract Animation<TextureRegion> all();
	public abstract Animation<TextureRegion> cornerA();
	public abstract Animation<TextureRegion> cornerB();
	public abstract Animation<TextureRegion> cornerAB();
	public abstract Animation<TextureRegion> cornerC();
	public abstract Animation<TextureRegion> cornerAC();
	public abstract Animation<TextureRegion> cornerBC();
	public abstract Animation<TextureRegion> cornerABC();
	public abstract Animation<TextureRegion> cornerD();
	public abstract Animation<TextureRegion> cornerAD();
	public abstract Animation<TextureRegion> cornerBD();
	public abstract Animation<TextureRegion> cornerABD();
	public abstract Animation<TextureRegion> cornerCD();
	public abstract Animation<TextureRegion> cornerACD();
	public abstract Animation<TextureRegion> cornerBCD();
	public abstract Animation<TextureRegion> cornerABCD();
	public abstract Animation<TextureRegion> none();
	public abstract Animation<TextureRegion> wallDecoration1();
	public abstract Animation<TextureRegion> wallDecoration2();
	public abstract Animation<TextureRegion> wallDecoration3();
	public abstract Animation<TextureRegion> wallDecoration4();
}
