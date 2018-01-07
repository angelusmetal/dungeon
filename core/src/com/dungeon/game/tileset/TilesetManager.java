package com.dungeon.game.tileset;

public class TilesetManager {

	private final DungeonVioletTileset dungeonVioletTileset;
	private final TombstoneTileset tombstoneTileset;
	private final GhostTileset ghostTileset;
	private final CharactersTileset32 charactersTileset;
	private final ProjectileTileset projectileTileset;
	private final CatProjectileTileset catProjectileTileset;
	private final HudTileset hudTileset;

	public TilesetManager() {
		dungeonVioletTileset = new DungeonVioletTileset();
		tombstoneTileset = new TombstoneTileset();
		ghostTileset = new GhostTileset();
		charactersTileset = new CharactersTileset32();
		projectileTileset = new ProjectileTileset();
		catProjectileTileset = new CatProjectileTileset();
		hudTileset = new HudTileset();
	}

	public DungeonVioletTileset getDungeonVioletTileset() {
		return dungeonVioletTileset;
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
	public CatProjectileTileset getCatProjectileTileset() {
		return catProjectileTileset;
	}
	public HudTileset getHudTileset() {
		return hudTileset;
	}

	public void dispose() {
		dungeonVioletTileset.dispose();
		ghostTileset.dispose();
		charactersTileset.dispose();
		projectileTileset.dispose();
		catProjectileTileset.dispose();
		hudTileset.dispose();
	}
}
