package com.dungeon.game.level.room;

import com.dungeon.game.level.Room;

public interface RoomGenerator {
	int minWidth();
	int maxWidth();
	int minHeight();
	int maxHeight();
	Room generate(int left, int bottom, int width, int height, int generation);
}
