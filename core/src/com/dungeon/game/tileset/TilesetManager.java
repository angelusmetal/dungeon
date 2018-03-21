package com.dungeon.game.tileset;

public class TilesetManager {

	private final DungeonVioletTileset dungeonVioletTileset;
	private final HudTileset hudTileset;

	public TilesetManager() {
		dungeonVioletTileset = new DungeonVioletTileset();
		hudTileset = new HudTileset();
	}

	public DungeonVioletTileset getDungeonVioletTileset() {
		return dungeonVioletTileset;
	}
	public HudTileset getHudTileset() {
		return hudTileset;
	}

	public void dispose() {
		dungeonVioletTileset.dispose();
		hudTileset.dispose();
	}
}
