package com.dungeon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DungeonTileset16 extends Tileset {

	public final TextureRegion PIT_UPPER_LEFT = getTile(0, 0);
	public final TextureRegion PIT_UPPER_CENTER = getTile(1, 0);
	public final TextureRegion PIT_UPPER_RIGHT = getTile(2, 0);
	public final TextureRegion PIT_LEFT = getTile(0, 1);
	public final TextureRegion PIT_CENTER = getTile(1, 1);
	public final TextureRegion PIT_RIGHT = getTile(2, 1);
	public final TextureRegion PIT_LOWER_LEFT = getTile(0, 2);
	public final TextureRegion PIT_LOWER_CENTER = getTile(1, 2);
	public final TextureRegion PIT_LOWER_RIGHT = getTile(2, 2);

	public final TextureRegion PIT_INNER_UPPER_LEFT = getTile(0, 3);
	public final TextureRegion PIT_INNER_UPPER_RIGHT = getTile(1, 3);
	public final TextureRegion PIT_INNER_LOWER_LEFT = getTile(0, 4);
	public final TextureRegion PIT_INNER_LOWER_RIGHT = getTile(1, 4);

	public final TextureRegion COLUMN = getTile(2, 3, 1, 2);
	public final TextureRegion COLUMN_LOWER_LEFT = getTile(3, 0);
	public final TextureRegion COLUMN_CENTER_LEFT = getTile(3, 1);
	public final TextureRegion COLUMN_UPPER_LEFT = getTile(3, 2);
	public final TextureRegion COLUMN_LOWER_RIGHT = getTile(4, 0);
	public final TextureRegion COLUMN_CENTER_RIGHT = getTile(4, 1);
	public final TextureRegion COLUMN_UPPER_RIGHT = getTile(4, 2);

	public final TextureRegion OUTER_WALL_UPPER_LEFT = getTile(5, 0);
	public final TextureRegion OUTER_WALL_UPPER_LEFT_FRONT = getTile(5, 1);
	public final TextureRegion OUTER_WALL_LEFT = getTile(5, 2);
	public final TextureRegion OUTER_WALL_LOWER_LEFT = getTile(5, 3);

	public final TextureRegion OUTER_WALL_TOP = getTile(6, 0);
	public final TextureRegion OUTER_WALL_TOP_FRONT = getTile(6, 1);
	public final TextureRegion FLOOR = getTile(6, 2);
	public final TextureRegion OUTER_WALL_BOTTOM = getTile(6, 3);

	public final TextureRegion OUTER_WALL_UPPER_RIGHT = getTile(7, 0);
	public final TextureRegion OUTER_WALL_UPPER_RIGHT_FRONT = getTile(7, 1);
	public final TextureRegion OUTER_WALL_RIGHT = getTile(7, 2);
	public final TextureRegion OUTER_WALL_LOWER_RIGHT = getTile(7, 3);

	public final TextureRegion INNER_WALL_UPPER_LEFT = getTile(8, 0);
	public final TextureRegion INNER_WALL_UPPER_LEFT_FRONT = getTile(8, 1);
	public final TextureRegion INNER_WALL_LEFT = getTile(8, 2);
	public final TextureRegion INNER_WALL_LOWER_LEFT = getTile(8, 3);

	public final TextureRegion INNER_WALL_UPPER_RIGHT = getTile(8, 0);
	public final TextureRegion INNER_WALL_UPPER_RIGHT_FRONT = getTile(8, 1);
	public final TextureRegion INNER_WALL_RIGHT = getTile(8, 2);
	public final TextureRegion INNER_WALL_LOWER_RIGHT = getTile(8, 3);

	public final TextureRegion TOTEM_A_DARK = getTile(10, 0, 2, 3);
	public final TextureRegion TOTEM_A_MID_BLUE = getTile(12, 0, 2, 3);
	public final TextureRegion TOTEM_A_LIGHT_BLUE = getTile(14, 0, 2, 3);
	public final TextureRegion TOTEM_A_MID_ORANGE = getTile(16, 0, 2, 3);
	public final TextureRegion TOTEM_A_LIGHT_ORANGE = getTile(18, 0, 2, 3);
	public final TextureRegion TOTEM_A_MID_GREEN = getTile(20, 0, 2, 3);
	public final TextureRegion TOTEM_A_LIGHT_GREEN = getTile(22, 0, 2, 3);

	public final TextureRegion TOTEM_B_DARK = getTile(10, 3, 2, 3);
	public final TextureRegion TOTEM_B_MID_BLUE = getTile(12, 3, 2, 3);
	public final TextureRegion TOTEM_B_LIGHT_BLUE = getTile(14, 3, 2, 3);
	public final TextureRegion TOTEM_B_MID_ORANGE = getTile(16, 3, 2, 3);
	public final TextureRegion TOTEM_B_LIGHT_ORANGE = getTile(18, 3, 2, 3);
	public final TextureRegion TOTEM_B_MID_GREEN = getTile(20, 3, 2, 3);
	public final TextureRegion TOTEM_B_LIGHT_GREEN = getTile(22, 3, 2, 3);

	public final TextureRegion SWITCH_1 = getTile(0, 5);
	public final TextureRegion SWITCH_2 = getTile(1, 5);
	public final TextureRegion SWITCH_3 = getTile(2, 5);
	public final TextureRegion SWITCH_4 = getTile(3, 5);
	public final TextureRegion SWITCH_5 = getTile(4, 5);
	public final TextureRegion SWITCH_6 = getTile(5, 5);
	public final TextureRegion SWITCH_7 = getTile(6, 5);

	public final TextureRegion GATE_1 = getTile(0, 6);
	public final TextureRegion GATE_2 = getTile(1, 6);
	public final TextureRegion GATE_3 = getTile(2, 6);
	public final TextureRegion GATE_4 = getTile(3, 6);
	public final TextureRegion GATE_5 = getTile(4, 6);

	public final TextureRegion DOOR_1 = getTile(4, 7);
	public final TextureRegion DOOR_2 = getTile(5, 7);
	public final TextureRegion DOOR_3 = getTile(6, 7);
	public final TextureRegion DOOR_4 = getTile(7, 7);

	public final TextureRegion CHEST_1 = getTile(8, 7);
	public final TextureRegion CHEST_2 = getTile(9, 7);
	public final TextureRegion CHEST_3 = getTile(10, 7);
	public final TextureRegion CHEST_4 = getTile(11, 7);
	public final TextureRegion CHEST_5 = getTile(12, 7);
	public final TextureRegion CHEST_6 = getTile(13, 7);

	public final TextureRegion DOUBLE_DOOR_1 = getTile(0, 8, 3, 2);
	public final TextureRegion DOUBLE_DOOR_2 = getTile(3, 8, 3, 2);
	public final TextureRegion DOUBLE_DOOR_3 = getTile(6, 8, 3, 2);
	public final TextureRegion DOUBLE_DOOR_4 = getTile(9, 8, 3, 2);
	public final TextureRegion DOUBLE_DOOR_5 = getTile(12, 8, 3, 2);

	public final TextureRegion PIT_SMALL_LADDER = getTile(14, 8, 2, 2);
	public final TextureRegion PIT_SMALL = getTile(16, 8, 2, 2);

	public final Animation<TextureRegion> TOTEM_A_BLUE_ANIMATION = new Animation<>(0.1f, TOTEM_A_DARK, TOTEM_A_MID_BLUE, TOTEM_A_LIGHT_BLUE, TOTEM_A_MID_BLUE);
	public final Animation<TextureRegion> TOTEM_A_ORANGE_ANIMATION = new Animation<>(0.1f, TOTEM_A_DARK, TOTEM_A_MID_ORANGE, TOTEM_A_LIGHT_ORANGE, TOTEM_A_MID_ORANGE);
	public final Animation<TextureRegion> TOTEM_A_GREEN_ANIMATION = new Animation<>(0.1f, TOTEM_A_DARK, TOTEM_A_MID_GREEN, TOTEM_A_LIGHT_GREEN, TOTEM_A_MID_GREEN);

	public final Animation<TextureRegion> TOTEM_B_BLUE_ANIMATION = new Animation<>(0.1f, TOTEM_B_DARK, TOTEM_B_MID_BLUE, TOTEM_B_LIGHT_BLUE, TOTEM_B_MID_BLUE);
	public final Animation<TextureRegion> TOTEM_B_ORANGE_ANIMATION = new Animation<>(0.1f, TOTEM_B_DARK, TOTEM_B_MID_ORANGE, TOTEM_B_LIGHT_ORANGE, TOTEM_B_MID_ORANGE);
	public final Animation<TextureRegion> TOTEM_B_GREEN_ANIMATION = new Animation<>(0.1f, TOTEM_B_DARK, TOTEM_B_MID_GREEN, TOTEM_B_LIGHT_GREEN, TOTEM_B_MID_GREEN);

	public final Animation<TextureRegion> SWITCH_ANIMATION = new Animation<>(0.1f, SWITCH_1, SWITCH_2, SWITCH_3, SWITCH_4, SWITCH_5, SWITCH_6, SWITCH_7);
	public final Animation<TextureRegion> SWITCH_REVERSE_ANIMATION = new Animation<>(0.1f, SWITCH_7, SWITCH_6, SWITCH_5, SWITCH_4, SWITCH_3, SWITCH_2, SWITCH_1);
	public final Animation<TextureRegion> GATE_OPEN_ANIMATION = new Animation<>(0.1f, GATE_5, GATE_4, GATE_3, GATE_2, GATE_1);
	public final Animation<TextureRegion> GATE_CLOSE_ANIMATION = new Animation<>(0.1f, GATE_1, GATE_2, GATE_3, GATE_4, GATE_5);
	public final Animation<TextureRegion> DOOR_OPEN_ANIMATION = new Animation<>(0.1f, DOOR_1, DOOR_2, DOOR_3, DOOR_4);
	public final Animation<TextureRegion> DOOR_CLOSE_ANIMATION = new Animation<>(0.1f, DOOR_4, DOOR_3, DOOR_2, DOOR_1);
	public final Animation<TextureRegion> CHEST_OPEN_ANIMATION = new Animation<>(0.1f, CHEST_1, CHEST_2, CHEST_3, CHEST_4, CHEST_5, CHEST_6);
	public final Animation<TextureRegion> CHEST_CLOSE_ANIMATION = new Animation<>(0.1f, CHEST_6, CHEST_5, CHEST_4, CHEST_3, CHEST_2, CHEST_1);
	public final Animation<TextureRegion> DOUBLE_DOOR_OPEN_ANIMATION = new Animation<>(0.1f, DOUBLE_DOOR_1, DOUBLE_DOOR_2, DOUBLE_DOOR_3, DOUBLE_DOOR_4, DOUBLE_DOOR_5);
	public final Animation<TextureRegion> DOUBLE_DOOR_CLOSE_ANIMATION = new Animation<>(0.1f, DOUBLE_DOOR_5, DOUBLE_DOOR_4, DOUBLE_DOOR_3, DOUBLE_DOOR_2, DOUBLE_DOOR_1);

	public final Tile PIT_UPPER_LEFT_TILE = new Tile(PIT_UPPER_LEFT);
	public final Tile PIT_UPPER_CENTER_TILE = new Tile(PIT_UPPER_CENTER);
	public final Tile PIT_UPPER_RIGHT_TILE = new Tile(PIT_UPPER_RIGHT);
	public final Tile PIT_LEFT_TILE = new Tile(PIT_LEFT);
	public final Tile PIT_CENTER_TILE = new Tile(PIT_CENTER);
	public final Tile PIT_RIGHT_TILE = new Tile(PIT_RIGHT);
	public final Tile PIT_LOWER_LEFT_TILE = new Tile(PIT_LOWER_LEFT);
	public final Tile PIT_LOWER_CENTER_TILE = new Tile(PIT_LOWER_CENTER);
	public final Tile PIT_LOWER_RIGHT_TILE = new Tile(PIT_LOWER_RIGHT);
	public final Tile CHEST_TILE = new Tile(CHEST_OPEN_ANIMATION);
	public final Tile FLOOR_TILE = new Tile(FLOOR);

	public DungeonTileset16() {
		super(new Texture("dungeon_sheet.png"), 16);
	}

}
