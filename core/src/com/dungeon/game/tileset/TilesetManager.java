package com.dungeon.game.tileset;

// TODO Remove this!
public class TilesetManager {

	private final HudSheet hudTileset;

	public TilesetManager() {
		hudTileset = new HudSheet();
	}

	public HudSheet getHudTileset() {
		return hudTileset;
	}

	public void dispose() {
		hudTileset.dispose();
	}
}
