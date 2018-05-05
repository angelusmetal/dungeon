package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.resource.ResourceManager;

public class DungeonVioletTileset extends LevelTileset {

	private final TextureRegion FLOOR_1 = getTile(6, 2);
	private final TextureRegion FLOOR_2 = getTile(6, 3);
	private final TextureRegion FLOOR_3 = getTile(7, 2);
	private final TextureRegion FLOOR_4 = getTile(7, 3);

	private final TextureRegion EXIT = getTile(6, 0);

	private final TextureRegion CONVEX_LOWER_RIGHT = getTile(1, 2);
	private final TextureRegion CONVEX_LOWER_LEFT = getTile(0, 2);
	private final TextureRegion CONVEX_UPPER_RIGHT = getTile(1, 3);
	private final TextureRegion CONVEX_UPPER_LEFT = getTile(0, 3);

	private final TextureRegion CONCAVE_UPPER_LEFT = getTile(0, 0);
	private final TextureRegion CONCAVE_UPPER_1 = getTile(2, 0);
	private final TextureRegion CONCAVE_UPPER_2 = getTile(3, 0);
	private final TextureRegion CONCAVE_UPPER_3 = getTile(4, 0);
	private final TextureRegion CONCAVE_UPPER_4 = getTile(5, 0);
	private final TextureRegion CONCAVE_UPPER_RIGHT = getTile(1, 0);
	private final TextureRegion CONCAVE_LEFT_1 = getTile(2, 2);
	private final TextureRegion CONCAVE_LEFT_2 = getTile(3, 2);
	private final TextureRegion CONCAVE_LEFT_3 = getTile(4, 2);
	private final TextureRegion CONCAVE_LEFT_4 = getTile(5, 2);
	private final TextureRegion CONCAVE_RIGHT_1 = getTile(2, 3);
	private final TextureRegion CONCAVE_RIGHT_2 = getTile(3, 3);
	private final TextureRegion CONCAVE_RIGHT_3 = getTile(4, 3);
	private final TextureRegion CONCAVE_RIGHT_4 = getTile(5, 3);
	private final TextureRegion CONCAVE_LOWER_LEFT = getTile(0, 1);
	private final TextureRegion CONCAVE_LOWER_1 = getTile(2, 1);
	private final TextureRegion CONCAVE_LOWER_2 = getTile(3, 1);
	private final TextureRegion CONCAVE_LOWER_3 = getTile(4, 1);
	private final TextureRegion CONCAVE_LOWER_4 = getTile(5, 1);
	private final TextureRegion CONCAVE_LOWER_RIGHT = getTile(1, 1);
	private final TextureRegion WALL_DECOR_1 = getTile(2, 4);
	private final TextureRegion WALL_DECOR_2 = getTile(3, 4);
	private final TextureRegion WALL_DECOR_3 = getTile(4, 4);
	private final TextureRegion WALL_DECOR_4 = getTile(5, 4);

//	public final TextureRegion OVERLAY_1 = getTile(0, 0);
//	public final TextureRegion OVERLAY_2 = getTile(0, 1);

	private final TextureRegion VOID = getTile(0, 4);

	private final Tile[] FLOOR_TILES = createTiles(FLOOR_1, FLOOR_2, FLOOR_3, FLOOR_4);

	private final Tile EXIT_TILE = new Tile(EXIT);

	private final Tile CONVEX_LOWER_RIGHT_TILE = new Tile(CONVEX_LOWER_RIGHT);
	private final Tile CONVEX_LOWER_LEFT_TILE = new Tile(CONVEX_LOWER_LEFT);
	private final Tile CONVEX_UPPER_RIGHT_TILE = new Tile(CONVEX_UPPER_RIGHT);
	private final Tile CONVEX_UPPER_LEFT_TILE = new Tile(CONVEX_UPPER_LEFT);

	private final Tile CONCAVE_UPPER_LEFT_TILE = new Tile(CONCAVE_UPPER_LEFT);
	private final Tile[] CONCAVE_UPPER_TILES = createTiles(CONCAVE_UPPER_1, CONCAVE_UPPER_2, CONCAVE_UPPER_3, CONCAVE_UPPER_4);
	private final Tile CONCAVE_UPPER_RIGHT_TILE = new Tile(CONCAVE_UPPER_RIGHT);
	private final Tile[] CONCAVE_LEFT_TILES = createTiles(CONCAVE_LEFT_1, CONCAVE_LEFT_2, CONCAVE_LEFT_3, CONCAVE_LEFT_4);
	private final Tile[] CONCAVE_RIGHT_TILES = createTiles(CONCAVE_RIGHT_1, CONCAVE_RIGHT_2, CONCAVE_RIGHT_3, CONCAVE_RIGHT_4);
	private final Tile CONCAVE_LOWER_LEFT_TILE = new Tile(CONCAVE_LOWER_LEFT);
	private final Tile[] CONCAVE_LOWER_TILES = createTiles(CONCAVE_LOWER_1, CONCAVE_LOWER_2, CONCAVE_LOWER_3, CONCAVE_LOWER_4);
	private final Tile CONCAVE_LOWER_RIGHT_TILE = new Tile(CONCAVE_LOWER_RIGHT);

	private final Tile WALL_DECOR_1_TILE = new Tile(WALL_DECOR_1);
	private final Tile WALL_DECOR_2_TILE = new Tile(WALL_DECOR_2);
	private final Tile WALL_DECOR_3_TILE = new Tile(WALL_DECOR_3);
	private final Tile WALL_DECOR_4_TILE = new Tile(WALL_DECOR_4);

//	private final Tile OVERLAY_1_TILE = new Tile(OVERLAY_1);
//	private final Tile OVERLAY_2_TILE = new Tile(OVERLAY_2);

	private final Tile VOID_TILE = new Tile(VOID);

	public DungeonVioletTileset() {
		super(ResourceManager.instance().getTexture("dungeon_violet.png"), 48);
	}

	@Override
	public Tile out() {
		return VOID_TILE;
	}

	@Override
	public Tile floor() {
		return Rand.pick(FLOOR_TILES);
	}

	@Override
	public Tile exit() {
		return EXIT_TILE;
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
		return Rand.pick(CONCAVE_LOWER_TILES);
	}

	@Override
	public Tile concaveUpper() {
		return Rand.pick(CONCAVE_UPPER_TILES);
	}

	@Override
	public Tile concaveLeft() {
		return Rand.pick(CONCAVE_LEFT_TILES);
	}

	@Override
	public Tile concaveRight() {
		return Rand.pick(CONCAVE_RIGHT_TILES);
	}

	@Override
	public Tile wallDecoration1() {
		return WALL_DECOR_1_TILE;
	}

	@Override
	public Tile wallDecoration2() {
		return WALL_DECOR_2_TILE;
	}

	@Override
	public Tile wallDecoration3() {
		return WALL_DECOR_3_TILE;
	}

	@Override
	public Tile wallDecoration4() {
		return WALL_DECOR_4_TILE;
	}
}
