package com.dungeon.game.state;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.game.character.acidslime.AcidSlimeFactory;
import com.dungeon.game.character.assassin.AssassinFactory;
import com.dungeon.game.character.fireslime.FireSlimeFactory;
import com.dungeon.game.character.ghost.GhostFactory;
import com.dungeon.game.character.slime.SlimeFactory;
import com.dungeon.game.character.thief.ThiefFactory;
import com.dungeon.game.character.witch.WitchFactory;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.object.props.DoorFactory;
import com.dungeon.game.object.exit.ExitPlatformFactory;
import com.dungeon.game.object.particle.ParticleFactory;
import com.dungeon.game.object.powerups.HealthPowerupFactory;
import com.dungeon.game.object.props.FurnitureFactory;
import com.dungeon.game.object.tombstone.TombstoneFactory;
import com.dungeon.game.object.torch.TorchFactory;
import com.dungeon.game.tileset.LevelTileset;
import com.dungeon.game.tileset.TilesetManager;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameState {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	private static final int BASE_MAP_WIDTH = 40;
	private static final int BASE_MAP_HEIGHT = 40;

	private static EntityFactory entityFactory;
	private static Toml configuration;

	private static float stateTime = 0;
	private static float frameTime;
	private static Level level;
	private static TilesetManager tilesetManager = new TilesetManager();
	private static State currentState = State.MENU;
	private static Console console;

	private static List<PlayerCharacter> playerCharacters = new LinkedList<>();
	private static List<PlayerCharacter> newPlayerCharacters = new LinkedList<>();

	private static List<Entity> entities = new LinkedList<>();
	private static List<Entity> newEntities = new LinkedList<>();

	private static List<CharacterSelection.Slot> slots;

	private static int playerCount;
	private static int levelCount;

	private static List<RenderEffect> renderEffects = new ArrayList<>();
	private static List<RenderEffect> newRenderEffects = new ArrayList<>();

	private static List<OverlayText> overlayTexts = new ArrayList<>();
	private static List<OverlayText> newOverelayTexts = new ArrayList<>();

	private static Runnable motionBlur;

	public static void initialize(Toml configuration) {
		GameState.entityFactory = new EntityFactory();
		GameState.configuration = configuration;
		console = new Console(10, 3f); // TODO get size from config
		initEntityFactories(entityFactory);
	}

	private static void initEntityFactories(EntityFactory entityFactory) {
		entityFactory.registerFactory(EntityType.EXIT, new ExitPlatformFactory());

		entityFactory.registerFactory(EntityType.TORCH, new TorchFactory());
		entityFactory.registerFactory(EntityType.TOMBSTONE, new TombstoneFactory());
		DoorFactory doorFactory = new DoorFactory();
		entityFactory.registerFactory(EntityType.DOOR_VERTICAL, doorFactory::buildVertical);
		entityFactory.registerFactory(EntityType.DOOR_HORIZONTAL, doorFactory::buildHorizontal);
		FurnitureFactory furnitureFactory = new FurnitureFactory();
		entityFactory.registerFactory(EntityType.BOOKSHELF, furnitureFactory::buildBookshelf);
		entityFactory.registerFactory(EntityType.TABLE, furnitureFactory::buildTable);
		entityFactory.registerFactory(EntityType.TABLE2, furnitureFactory::buildTable2);
		entityFactory.registerFactory(EntityType.CAGE, furnitureFactory::buildCage);

		entityFactory.registerFactory(EntityType.GHOST, new GhostFactory());
		entityFactory.registerFactory(EntityType.SLIME, new SlimeFactory());
		entityFactory.registerFactory(EntityType.SLIME_ACID, new AcidSlimeFactory());
		entityFactory.registerFactory(EntityType.SLIME_FIRE, new FireSlimeFactory());

		entityFactory.registerFactory(EntityType.HEALTH_POWERUP, new HealthPowerupFactory());

		entityFactory.registerFactory(EntityType.ASSASIN, new AssassinFactory());
		entityFactory.registerFactory(EntityType.THIEF, new ThiefFactory());
		entityFactory.registerFactory(EntityType.WITCH, new WitchFactory());

		ParticleFactory particleFactory = new ParticleFactory();
		entityFactory.registerFactory(EntityType.WOOD_PARTICLE, particleFactory::buildWoodParticle);
		entityFactory.registerFactory(EntityType.STONE_PARTICLE, particleFactory::buildStoneParticle);
		entityFactory.registerFactory(EntityType.DROPLET_PARTICLE, particleFactory::buildDroplet);
		entityFactory.registerFactory(EntityType.FIREBALL_PARTICLE, particleFactory::buildFireball);
		entityFactory.registerFactory(EntityType.FLAME_PARTICLE, particleFactory::buildFlame);
		entityFactory.registerFactory(EntityType.CANDLE_PARTICLE, particleFactory::buildCandle);
	}

	public static float time() {
		return stateTime;
	}

	public static float frameTime() {
		return frameTime;
	}

	public static Toml getConfiguration() {
		return configuration;
	}

	public static Console console() {
		return console;
	}

	public static Entity build(EntityType type, Vector2 position) {
		return entityFactory.build(type, position);
	}

	public static void addTime(float frameTime) {
		stateTime += frameTime;
		GameState.frameTime = frameTime;
		Light.updateDimmers(frameTime);
	}

	public static Level getLevel() {
		return level;
	}

	public static TilesetManager getTilesetManager() {
		return tilesetManager;
	}

	public static State getCurrentState() {
		return currentState;
	}
	public static void setCurrentState(GameState.State newState) {
		currentState = newState;
	}

	public static int getPlayerCount() {
		return playerCount;
	}

	public static int getLevelCount() {
		return levelCount;
	}

	public static void startNewGame(List<CharacterSelection.Slot> slots) {
		GameState.slots = slots;
		levelCount = 0;
		startNewLevel();
	}

	public static void exitLevel() {
		addRenderEffect(FadeEffect.fadeOut(time(), GameState::startNewLevel));
	}

	public static void startNewLevel() {

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

		addRenderEffect(FadeEffect.fadeIn(time()));
	}

	private static PlayerCharacter createCharacter(int characterId, Vector2 origin) {
		if (characterId == 0) {
			return (PlayerCharacter) entityFactory.build(EntityType.WITCH, origin);
		} else if (characterId == 1) {
			return (PlayerCharacter) entityFactory.build(EntityType.THIEF, origin);
		} else {
			return (PlayerCharacter) entityFactory.build(EntityType.ASSASIN, origin);
		}
	}

	public static LevelTileset getLevelTileset() {
		return tilesetManager.getDungeonVioletTileset();
	}

	public static void generateNewLevel() {
		int baseWidth = configuration.getLong("map.width", (long) BASE_MAP_WIDTH).intValue();
		int baseHeight = configuration.getLong("map.width", (long) BASE_MAP_HEIGHT).intValue();
		int growth = configuration.getLong("map.growth", (long) 10).intValue();
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(configuration, baseWidth + levelCount * growth, baseHeight + levelCount * growth);
		level = generator.generateLevel(getLevelTileset());
	}
	public static void addEntity(Entity entity) {
		newEntities.add(entity);
	}

	public static void addPlayerCharacter(PlayerCharacter character) {
		newPlayerCharacters.add(character);
		newEntities.add(character);
	}

	public static void addOverlayText(OverlayText overlayText) {
		newOverelayTexts.add(overlayText);
	}

	public static List<Entity> getEntities() {
		return entities;
	}

	public static List<PlayerCharacter> getPlayerCharacters() {
		return playerCharacters;
	}

	public static List<OverlayText> getOverlayTexts() {
		return overlayTexts;
	}

	public static void addRenderEffect(RenderEffect effect) {
		newRenderEffects.add(effect);
	}

	public static List<RenderEffect> getRenderEffects() {
		return renderEffects;
	}

	public static void refresh() {
		playerCharacters.addAll(newPlayerCharacters);
		newPlayerCharacters.clear();
		entities.addAll(newEntities);
		newEntities.clear();
		overlayTexts.addAll(newOverelayTexts);
		newOverelayTexts.clear();

		// Dispose and remove old effects, and then add new ones
		renderEffects.stream().filter(RenderEffect::isExpired).forEach(RenderEffect::dispose);
		renderEffects.removeIf(RenderEffect::isExpired);
		renderEffects.addAll(newRenderEffects);
		newRenderEffects.clear();
	}

	public static int playersAlive() {
		return playerCharacters.size() + newPlayerCharacters.size();
	}

	public static void setMotionBlur(Runnable runnable) {
		GameState.motionBlur = runnable;
	}

	public static void doMotionBlur() {
		motionBlur.run();
	}
}
