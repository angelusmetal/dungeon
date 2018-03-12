package com.dungeon.game.state;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.TilesetHelper;
import com.dungeon.game.character.Assasin;
import com.dungeon.game.character.Thief;
import com.dungeon.game.character.Witch;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.tileset.LevelTileset;
import com.dungeon.game.tileset.TilesetManager;

import java.util.LinkedList;
import java.util.List;

public class GameState {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	public final int MAP_WIDTH = 100;
	public final int MAP_HEIGHT = 100;
	private final TilesetHelper tilesetHelper;
	private final EntityFactory entityFactory;

	private float stateTime;
	private float frameTime;
	private Level level;
	private TilesetManager tilesetManager;
	private ViewPort viewPort;
	private State currentState = State.MENU;

	private List<PlayerCharacter> playerCharacters = new LinkedList<>();
	private List<Entity<?>> entities = new LinkedList<>();

	private List<PlayerCharacter> newPlayerCharacters = new LinkedList<>();
	private List<Entity<?>> newEntities = new LinkedList<>();

	private int playerCount;

	public GameState(ViewPort viewPort, EntityFactory entityFactory) {
		this.stateTime = 0;
		this.viewPort = viewPort;
		this.entityFactory = entityFactory;
		this.tilesetManager = new TilesetManager();
		this.tilesetHelper = new TilesetHelper(getLevelTileset());
	}

	public float getStateTime() {
		return stateTime;
	}

	public float getFrameTime() {
		return frameTime;
	}

	public void updateStateTime(float frameTime) {
		this.stateTime += frameTime;
		this.frameTime = frameTime;
		Light.updateDimmers(frameTime);
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

	public State getCurrentState() {
		return currentState;
	}
	public void setCurrentState(GameState.State newState) {
		currentState = newState;
	}

	public int getPlayerCount() {
		return playerCount;
	}
	public void startNewLevel(List<CharacterSelection.Slot> slots) {
		generateNewLevel();

		// Get starting room and spawn players there
		Room startingRoom = level.rooms.get(0);
		int spawnPoint = 0;
		for (CharacterSelection.Slot slot : slots) {
			PlayerCharacter character = createCharacter(slot.characterId, startingRoom.spawnPoints.get(spawnPoint++).cpy().scl(getLevelTileset().tile_size));
			addPlayerCharacter(character);
			slot.control.setCharacter(character);
		}
		// Update player count
		playerCount = slots.size();

		// Instantiate entities for every placeholder
		for (EntityPlaceholder placeholder : level.entityPlaceholders) {
			addEntity(entityFactory.build(placeholder.getType(), placeholder.getOrigin().cpy().scl(getLevelTileset().tile_size)));
		}

		setCurrentState(GameState.State.INGAME);
	}

	private PlayerCharacter createCharacter(int characterId, Vector2 origin) {
		if (characterId == 0) {
			return new Witch.Factory(this).build(origin);
		} else if (characterId == 1) {
			return new Thief.Factory(this).build(origin);
		} else {
			return new Assasin.Factory(this).build(origin);
		}
	}

	public LevelTileset getLevelTileset() {
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
