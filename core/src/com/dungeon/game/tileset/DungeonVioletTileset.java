package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.render.Tileset;

public class DungeonVioletTileset extends LevelTileset {

	public final TextureRegion FLOOR_1 = getTile(6, 2);
	public final TextureRegion FLOOR_2 = getTile(6, 3);
	public final TextureRegion FLOOR_3 = getTile(7, 2);
	public final TextureRegion FLOOR_4 = getTile(7, 3);

	public final TextureRegion CONVEX_LOWER_RIGHT = getTile(1, 2);
	public final TextureRegion CONVEX_LOWER_LEFT = getTile(0, 2);
	public final TextureRegion CONVEX_UPPER_RIGHT = getTile(1, 3);
	public final TextureRegion CONVEX_UPPER_LEFT = getTile(0, 3);

	public final TextureRegion CONCAVE_UPPER_LEFT = getTile(0, 0);
	public final TextureRegion CONCAVE_UPPER = getTile(2, 0);
	public final TextureRegion CONCAVE_UPPER_RIGHT = getTile(1, 0);
	public final TextureRegion CONCAVE_LEFT = getTile(2, 2);
	public final TextureRegion CONCAVE_RIGHT = getTile(2, 3);
	public final TextureRegion CONCAVE_LOWER_LEFT = getTile(0, 1);
	public final TextureRegion CONCAVE_LOWER = getTile(2, 1);
	public final TextureRegion CONCAVE_LOWER_RIGHT = getTile(1, 1);

	// TODO map variants for UPPER, LEFT, RIGHT, LOWER

//	public final TextureRegion OVERLAY_1 = getTile(0, 0);
//	public final TextureRegion OVERLAY_2 = getTile(0, 1);

	public final TextureRegion VOID = getTile(0, 4);

	public final Tile FLOOR_1_TILE = new Tile(FLOOR_1);
	public final Tile FLOOR_2_TILE = new Tile(FLOOR_2);
	public final Tile FLOOR_3_TILE = new Tile(FLOOR_3);
	public final Tile FLOOR_4_TILE = new Tile(FLOOR_4);
	public final Tile[] FLOOR_TILES = {FLOOR_1_TILE, FLOOR_2_TILE, FLOOR_3_TILE, FLOOR_4_TILE};

	public final Tile CONVEX_LOWER_RIGHT_TILE = new Tile(CONVEX_LOWER_RIGHT);
	public final Tile CONVEX_LOWER_LEFT_TILE = new Tile(CONVEX_LOWER_LEFT);
	public final Tile CONVEX_UPPER_RIGHT_TILE = new Tile(CONVEX_UPPER_RIGHT);
	public final Tile CONVEX_UPPER_LEFT_TILE = new Tile(CONVEX_UPPER_LEFT);

	public final Tile CONCAVE_UPPER_LEFT_TILE = new Tile(CONCAVE_UPPER_LEFT);
	public final Tile CONCAVE_UPPER_TILE = new Tile(CONCAVE_UPPER);
	public final Tile CONCAVE_UPPER_RIGHT_TILE = new Tile(CONCAVE_UPPER_RIGHT);
	public final Tile CONCAVE_LEFT_TILE = new Tile(CONCAVE_LEFT);
	public final Tile CONCAVE_RIGHT_TILE = new Tile(CONCAVE_RIGHT);
	public final Tile CONCAVE_LOWER_LEFT_TILE = new Tile(CONCAVE_LOWER_LEFT);
	public final Tile CONCAVE_LOWER_TILE = new Tile(CONCAVE_LOWER);
	public final Tile CONCAVE_LOWER_RIGHT_TILE = new Tile(CONCAVE_LOWER_RIGHT);

//	public final Tile OVERLAY_1_TILE = new Tile(OVERLAY_1);
//	public final Tile OVERLAY_2_TILE = new Tile(OVERLAY_2);

	public final Tile VOID_TILE = new Tile(VOID);

	public DungeonVioletTileset() {
		super(new Texture("dungeon_violet.png"), 48);
	}

	@Override
	public Tile out() {
		return VOID_TILE;
	}

	@Override
	public Tile floor() {
		return getRandomTile(FLOOR_TILES);
	}

	@Override
	public Tile convexLowerLeft() {
		return CONVEX_LOWER_LEFT_TILE;
	}

	@Override
	public Tile convexLowerRight() {
		return CONVEX_LOWER_RIGHT_TILE;
	}

	@Override
	public Tile convexUpperLeft() {
		return CONVEX_UPPER_LEFT_TILE;
	}

	@Override
	public Tile convexUpperRight() {
		return CONVEX_UPPER_RIGHT_TILE;
	}

	@Override
	public Tile concaveLowerLeft() {
		return CONCAVE_LOWER_LEFT_TILE;
	}

	@Override
	public Tile concaveLowerRight() {
		return CONCAVE_LOWER_RIGHT_TILE;
	}

	@Override
	public Tile concaveUpperLeft() {
		return CONCAVE_UPPER_LEFT_TILE;
	}

	@Override
	public Tile concaveUpperRight() {
		return CONCAVE_UPPER_RIGHT_TILE;
	}

	@Override
	public Tile concaveLower() {
		return CONCAVE_LOWER_TILE;
	}

	@Override
	public Tile concaveUpper() {
		return CONCAVE_UPPER_TILE;
	}

	@Override
	public Tile concaveLeft() {
		return CONCAVE_LEFT_TILE;
	}

	@Override
	public Tile concaveRight() {
		return CONCAVE_RIGHT_TILE;
	}
}
