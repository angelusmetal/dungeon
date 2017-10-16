package com.dungeon.tileset;

public class TilesetManager {

	private final DungeonTilesetDark dungeonTilesetDark;
	private final GhostTileset ghostTileset;
	private final CharactersTileset32 charactersTileset;
	private final ProjectileTileset projectileTileset;

	// TODO Can we do this on the constructor or for some engine reason we must defer this to "creation" time?
	public TilesetManager() {
		dungeonTilesetDark = new DungeonTilesetDark();
		ghostTileset = new GhostTileset();
		charactersTileset = new CharactersTileset32();
		projectileTileset = new ProjectileTileset();
	}

	public DungeonTilesetDark getDungeonTilesetDark() {
		return dungeonTilesetDark;
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

	public void dispose() {
		dungeonTilesetDark.dispose();
		ghostTileset.dispose();
		charactersTileset.dispose();
		projectileTileset.dispose();
	}
}
