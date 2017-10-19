package com.dungeon.tileset;

public class TilesetManager {

	private final DungeonTilesetDark dungeonTilesetDark;
	private final TombstoneTileset tombstoneTileset;
	private final GhostTileset ghostTileset;
	private final CharactersTileset32 charactersTileset;
	private final ProjectileTileset projectileTileset;
	private final HudTileset hudTileset;

	public TilesetManager() {
		dungeonTilesetDark = new DungeonTilesetDark();
		tombstoneTileset = new TombstoneTileset();
		ghostTileset = new GhostTileset();
		charactersTileset = new CharactersTileset32();
		projectileTileset = new ProjectileTileset();
		hudTileset = new HudTileset();
	}

	public DungeonTilesetDark getDungeonTilesetDark() {
		return dungeonTilesetDark;
	}
	public TombstoneTileset getTombstoneTileset() {
		return tombstoneTileset;
	}
	public GhostTileset getGhostTileset() {
		return ghostTileset;
	}
	public CharactersTileset32 getCharactersTileset() {
		return charactersTileset;
	}
	public ProjectileTileset getProjectileTileset() {
		return projectileTileset;
	}
	public HudTileset getHudTileset() {
		return hudTileset;
	}

	public void dispose() {
		dungeonTilesetDark.dispose();
		ghostTileset.dispose();
		charactersTileset.dispose();
		projectileTileset.dispose();
		hudTileset.dispose();
	}
}
