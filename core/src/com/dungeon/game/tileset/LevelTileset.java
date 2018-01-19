package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.render.Tileset;

public abstract class LevelTileset extends Tileset {

	public LevelTileset(Texture texture, int tile_size) {
		super(texture, tile_size);
	}

	public abstract Tile out();
	public abstract Tile floor();
	public abstract Tile convexLowerLeft();
	public abstract Tile convexLowerRight();
	public abstract Tile convexUpperLeft();
	public abstract Tile convexUpperRight();
	public abstract Tile concaveLowerLeft();
	public abstract Tile concaveLowerRight();
	public abstract Tile concaveUpperLeft();
	public abstract Tile concaveUpperRight();
	public abstract Tile concaveLower();
	public abstract Tile concaveUpper();
	public abstract Tile concaveLeft();
	public abstract Tile concaveRight();

}
