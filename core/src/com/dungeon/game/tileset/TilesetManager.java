package com.dungeon.game.tileset;

public class TilesetManager {

	private final DungeonVioletTileset dungeonVioletTileset;
	private final HudSheet hudTileset;

	public TilesetManager() {
		dungeonVioletTileset = new DungeonVioletTileset();
		hudTileset = new HudSheet();
	}

	public DungeonVioletTileset getDungeonVioletTileset() {
		return dungeonVioletTileset;
	}
	public HudSheet getHudTileset() {
		return hudTileset;
	}

	public void dispose() {
		dungeonVioletTileset.dispose();
		hudTileset.dispose();
	}
}
