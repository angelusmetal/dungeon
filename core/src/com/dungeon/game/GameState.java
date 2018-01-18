package com.dungeon.game;

import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.tileset.DungeonVioletTileset;
import com.dungeon.game.tileset.TilesetManager;

import java.util.LinkedList;
import java.util.List;

public class GameState {
	public final int MAP_WIDTH = 100;
	public final int MAP_HEIGHT = 100;
	private final TilesetHelper tilesetHelper;

	private float stateTime;
	private Level level;
	private TilesetManager tilesetManager;
	private ViewPort viewPort;

	private List<PlayerCharacter> playerCharacters = new LinkedList<>();
	private List<Entity<?>> entities = new LinkedList<>();

	private List<PlayerCharacter> newPlayerCharacters = new LinkedList<>();
	private List<Entity<?>> newEntities = new LinkedList<>();

	public GameState(ViewPort viewPort) {
		this.stateTime = 0;
		this.tilesetManager = new TilesetManager();
		this.viewPort = viewPort;
		this.tilesetHelper = new TilesetHelper(getLevelTileset());
	}

	public float getStateTime() {
		return stateTime;
	}

	public void updateStateTime(float deltaTime) {
		stateTime += deltaTime;
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

	public DungeonVioletTileset getLevelTileset() {
		// TODO Should DungeonVioletTileset implement a generic level Tileset class/interface?
		return tilesetManager.getDungeonVioletTileset();
	}

	public TilesetHelper getTilesetHelper() {
		return tilesetHelper;
	}

	public void generateNewLevel() {
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(MAP_WIDTH, MAP_HEIGHT);
		level = generator.generateLevel(getLevelTileset());
	}
	public void addEntity(Entity<?> entity) {
		newEntities.add(entity);
	}

	public void addPlayerCharacter(PlayerCharacter character) {
		newPlayerCharacters.add(character);
		newEntities.add(character);
	}

	public List<Entity<?>> getEntities() {
		return entities;
	}

	public List<PlayerCharacter> getPlayerCharacters() {
		return playerCharacters;
	}

	public void refresh() {
		playerCharacters.addAll(newPlayerCharacters);
		newPlayerCharacters.clear();
		entities.addAll(newEntities);
		newEntities.clear();
	}
}
