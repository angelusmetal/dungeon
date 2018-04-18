package com.dungeon.game.state;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.game.TilesetHelper;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.tileset.LevelTileset;
import com.dungeon.game.tileset.TilesetManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameState {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	public final int MAP_WIDTH = 40;
	public final int MAP_HEIGHT = 40;
	private final TilesetHelper tilesetHelper;
	private final EntityFactory entityFactory;

	private float stateTime;
	private float frameTime;
	private Level level;
	private TilesetManager tilesetManager;
	private State currentState = State.MENU;

	private List<PlayerCharacter> playerCharacters = new LinkedList<>();
	private List<Entity> entities = new LinkedList<>();

	private List<PlayerCharacter> newPlayerCharacters = new LinkedList<>();
	private List<Entity> newEntities = new LinkedList<>();

	private List<CharacterSelection.Slot> slots;

	private int playerCount;
	private int levelCount;

	private List<RenderEffect> renderEffects = new ArrayList<>();
	private List<RenderEffect> newRenderEffects = new ArrayList<>();

	public GameState(EntityFactory entityFactory) {
		this.stateTime = 0;
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

	public EntityFactory getEntityFactory() {
		return entityFactory;
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

	public State getCurrentState() {
		return currentState;
	}
	public void setCurrentState(GameState.State newState) {
		currentState = newState;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public int getLevelCount() {
		return levelCount;
	}

	public void startNewGame(List<CharacterSelection.Slot> slots) {
		this.slots = slots;
		levelCount = 0;
		startNewLevel();
	}

	public void exitLevel() {
		addRenderEffect(FadeEffect.fadeOut(getStateTime(), this::startNewLevel));
	}

	public void startNewLevel() {

		playerCharacters.clear();
		entities.clear();

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
		levelCount++;

		// Instantiate entities for every placeholder
		for (EntityPlaceholder placeholder : level.entityPlaceholders) {
			addEntity(entityFactory.build(placeholder.getType(), placeholder.getOrigin().cpy().scl(getLevelTileset().tile_size)));
		}

		setCurrentState(GameState.State.INGAME);

		addRenderEffect(FadeEffect.fadeIn(getStateTime()));
	}

	private PlayerCharacter createCharacter(int characterId, Vector2 origin) {
		if (characterId == 0) {
			return (PlayerCharacter) entityFactory.build(EntityType.WITCH, origin);
		} else if (characterId == 1) {
			return (PlayerCharacter) entityFactory.build(EntityType.THIEF, origin);
		} else {
			return (PlayerCharacter) entityFactory.build(EntityType.ASSASIN, origin);
		}
	}

	public LevelTileset getLevelTileset() {
		return tilesetManager.getDungeonVioletTileset();
	}

	public TilesetHelper getTilesetHelper() {
		return tilesetHelper;
	}

	public void generateNewLevel() {
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(MAP_WIDTH + (levelCount * 10), MAP_HEIGHT + (levelCount * 10));
		level = generator.generateLevel(getLevelTileset());
	}
	public void addEntity(Entity entity) {
		newEntities.add(entity);
	}

	public void addPlayerCharacter(PlayerCharacter character) {
		newPlayerCharacters.add(character);
		newEntities.add(character);
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<PlayerCharacter> getPlayerCharacters() {
		return playerCharacters;
	}

	public void addRenderEffect(RenderEffect effect) {
		newRenderEffects.add(effect);
	}

	public List<RenderEffect> getRenderEffects() {
		return renderEffects;
	}

	public void refresh() {
		playerCharacters.addAll(newPlayerCharacters);
		newPlayerCharacters.clear();
		entities.addAll(newEntities);
		newEntities.clear();

		for (Iterator<RenderEffect> e = renderEffects.iterator(); e.hasNext(); ) {
			RenderEffect effect = e.next();
			if (effect.isExpired(stateTime)) {
				e.remove();
				effect.dispose();
			}
		}
		renderEffects.addAll(newRenderEffects);
		newRenderEffects.clear();
	}

	public int playersAlive() {
		return playerCharacters.size() + newPlayerCharacters.size();
	}
}
