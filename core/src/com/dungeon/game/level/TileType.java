package com.dungeon.game.level;

public enum TileType {
	FLOOR(true),
	VOID(false),
	EXIT(true),
	WALL_DECORATION_1(false),
	WALL_DECORATION_2(false),
	WALL_DECORATION_3(false),
	WALL_DECORATION_4(false),
//	DOOR,
//	CARPET,
//	LAVA,
//	WATER
	;

	private final boolean isFloor;

	TileType(boolean isFloor) {
		this.isFloor = isFloor;
	}

	public boolean isFloor() {
		return isFloor;
	}
}
