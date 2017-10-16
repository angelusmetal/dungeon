package com.dungeon;

import com.dungeon.character.Character;
import com.dungeon.character.Projectile;
import com.dungeon.level.Level;
import com.dungeon.level.ProceduralLevelGenerator;
import com.dungeon.tileset.DungeonTilesetDark;
import com.dungeon.tileset.Tileset;
import com.dungeon.tileset.TilesetManager;
import com.dungeon.viewport.ViewPort;

import java.util.ArrayList;
import java.util.List;

public class GameState {
	public final int MAP_WIDTH = 100;
	public final int MAP_HEIGHT = 100;

	private float stateTime;
	private Level level;
	private TilesetManager tilesetManager;
	private ViewPort viewPort;

	private List<Character> characters = new ArrayList<>();
	public List<Projectile> projectiles = new ArrayList<>();

	public GameState(ViewPort viewPort) {
		this.stateTime = 0;
		this.tilesetManager = new TilesetManager();
		this.viewPort = viewPort;
	}

	public float getStateTime() {
		return stateTime;
	}

	public Level getLevel() {
		return level;
	}

	public TilesetManager getTilesetManager() {
		return tilesetManager;
	}

	public ViewPort getViewPort() {
		return viewPort;
	}

	public DungeonTilesetDark getLevelTileset() {
		// TODO Should DungeonTilesetDark implement a generic level Tileset class/interface?
		return tilesetManager.getDungeonTilesetDark();
	}

	public void generateNewLevel() {
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(MAP_WIDTH, MAP_HEIGHT);
		level = generator.generateLevel(getLevelTileset());
	}

	public void addCharacter(Character character) {
		characters.add(character);
	}

	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}

	public List<Character> getCharacters() {
		return characters;
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}
}
