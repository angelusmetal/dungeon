package com.dungeon.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
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
import com.dungeon.game.object.exit.ExitPlatformFactory;
import com.dungeon.game.object.particle.ParticleFactory;
import com.dungeon.game.object.powerups.HealthPowerupFactory;
import com.dungeon.game.object.props.DoorFactory;
import com.dungeon.game.object.props.FurnitureFactory;
import com.dungeon.game.object.tombstone.TombstoneFactory;
import com.dungeon.game.object.torch.TorchFactory;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.player.Player;
import com.dungeon.game.render.ViewPortRenderer;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.TilesetManager;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameState {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	private static final int BASE_MAP_WIDTH = 40;
	private static final int BASE_MAP_HEIGHT = 40;
	public static final List<String> DEFAULT_ITEM_TYPES = Arrays.asList("HEALTH_POWERUP", "WEAPON_SWORD", "WEAPON_CAT_STAFF", "WEAPON_GREEN_STAFF");

	private static EntityFactory entityFactory;
	private static Toml configuration;

	private static float stateTime = 0;
	private static float frameTime;
	private static Level level;
	private static TilesetManager tilesetManager = new TilesetManager();
	private static Environment levelSpec;
	private static State currentState = State.MENU;

	private static List<PlayerEntity> playerCharacters = new LinkedList<>();
	private static List<PlayerEntity> newPlayerCharacters = new LinkedList<>();

	private static List<Entity> entities = new LinkedList<>();
	private static List<Entity> newEntities = new LinkedList<>();

	private static List<Player> players;

	private static int playerCount;
	private static int levelCount;

	private static List<RenderEffect> renderEffects = new ArrayList<>();
	private static List<RenderEffect> newRenderEffects = new ArrayList<>();

	private static List<OverlayText> overlayTexts = new ArrayList<>();
	private static List<OverlayText> newOverelayTexts = new ArrayList<>();

	private static List<EntityType> lootSet;

	// FIXME Does this belong here?
	private static Color baseLight = Color.WHITE.cpy();

	public static void initialize(Toml configuration) {
		GameState.entityFactory = new EntityFactory();
		GameState.configuration = configuration;
		GameState.lootSet = configuration.getList("map.items", DEFAULT_ITEM_TYPES).stream().map(EntityType::valueOf).collect(Collectors.toList());
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
		entityFactory.registerFactory(EntityType.CHEST, furnitureFactory::buildChest);
		entityFactory.registerFactory(EntityType.COIN, furnitureFactory::buildCoin);
		entityFactory.registerFactory(EntityType.PAINTING_1, furnitureFactory::buildPainting1);
		entityFactory.registerFactory(EntityType.PAINTING_2, furnitureFactory::buildPainting2);
		entityFactory.registerFactory(EntityType.PAINTING_3, furnitureFactory::buildPainting3);
		entityFactory.registerFactory(EntityType.BUSH_GREEN, furnitureFactory::buildBushGreen);
		entityFactory.registerFactory(EntityType.BUSH_GOLD, furnitureFactory::buildBushGold);
		entityFactory.registerFactory(EntityType.BUSH_RED, furnitureFactory::buildBushRed);
		entityFactory.registerFactory(EntityType.BUSH_CYAN, furnitureFactory::buildBushCyan);
		entityFactory.registerFactory(EntityType.BUSH_PURPLE, furnitureFactory::buildBushPurple);
		entityFactory.registerFactory(EntityType.BUSH_GREEN_SMALL, furnitureFactory::buildBushGreenSmall);
		entityFactory.registerFactory(EntityType.BUSH_GOLD_SMALL, furnitureFactory::buildBushGoldSmall);
		entityFactory.registerFactory(EntityType.BUSH_RED_SMALL, furnitureFactory::buildBushRedSmall);
		entityFactory.registerFactory(EntityType.BUSH_CYAN_SMALL, furnitureFactory::buildBushCyanSmall);
		entityFactory.registerFactory(EntityType.BUSH_PURPLE_SMALL, furnitureFactory::buildBushPurpleSmall);
		entityFactory.registerFactory(EntityType.GRASS_1, furnitureFactory::buildGrass1);
		entityFactory.registerFactory(EntityType.GRASS_2, furnitureFactory::buildGrass2);
		entityFactory.registerFactory(EntityType.GRASS_3, furnitureFactory::buildGrass3);
		entityFactory.registerFactory(EntityType.FLOWER_1, furnitureFactory::buildFlower1);

		entityFactory.registerFactory(EntityType.GHOST, new GhostFactory());
		entityFactory.registerFactory(EntityType.SLIME, new SlimeFactory());
		entityFactory.registerFactory(EntityType.SLIME_ACID, new AcidSlimeFactory());
		entityFactory.registerFactory(EntityType.SLIME_FIRE, new FireSlimeFactory());

		entityFactory.registerFactory(EntityType.HEALTH_POWERUP, new HealthPowerupFactory());
		WeaponFactory weaponFactory = new WeaponFactory();
		entityFactory.registerFactory(EntityType.WEAPON_SWORD, weaponFactory::buildSword);
		entityFactory.registerFactory(EntityType.WEAPON_CAT_STAFF, weaponFactory::buildCatStaff);
		entityFactory.registerFactory(EntityType.WEAPON_GREEN_STAFF, weaponFactory::buildGreenStaff);

		entityFactory.registerFactory(EntityType.ASSASSIN, new AssassinFactory());
		entityFactory.registerFactory(EntityType.THIEF, new ThiefFactory());
		entityFactory.registerFactory(EntityType.WITCH, new WitchFactory());

		ParticleFactory particleFactory = new ParticleFactory();
		entityFactory.registerFactory(EntityType.WOOD_PARTICLE, particleFactory::buildWoodParticle);
		entityFactory.registerFactory(EntityType.STONE_PARTICLE, particleFactory::buildStoneParticle);
		entityFactory.registerFactory(EntityType.DROPLET_PARTICLE, particleFactory::buildDroplet);
		entityFactory.registerFactory(EntityType.FIREBALL_PARTICLE, particleFactory::buildFireball);
		entityFactory.registerFactory(EntityType.FLAME_PARTICLE, particleFactory::buildFlame);
		entityFactory.registerFactory(EntityType.CANDLE_PARTICLE, particleFactory::buildCandle);
		entityFactory.registerFactory(EntityType.LEAVE_PARTICLE, particleFactory::buildLeave);
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

	public static Entity build(EntityType type, Vector2 position) {
		return entityFactory.build(type, position);
	}

	public static void addTime(float frameTime) {
		stateTime += frameTime;
		GameState.frameTime = frameTime;
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

	public static List<Player> getPlayers() {
		return players;
	}

	public static int getPlayerCount() {
		return playerCount;
	}

	public static int getLevelCount() {
		return levelCount;
	}

	public static float getDifficultyTier() {
		double playerMultiplier = Math.pow(1.5, Math.max(getPlayerCount(), 1) - 1);
		double levelMultiplier = Math.pow(1.5, Math.max(getLevelCount(), 1) - 1);
		return (float) (playerMultiplier * levelMultiplier);
	}

	public static void startNewGame(List<Player> players) {
		GameState.players = players;
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

		// Update player count
		playerCount = players.size();
		levelCount++;

		// Get starting room and spawn players there
		Room startingRoom = level.rooms.get(0);
		int spawnPoint = 0;
		for (Player player : players) {
			Vector2 origin = startingRoom.spawnPoints.get(spawnPoint++).cpy().scl(levelSpec.getTileset().tile_size);
			player.spawn(origin);
			origin.y += 30;
			addOverlayText(new OverlayText(origin, getWelcomeMessage()).spell(1, 1).fadeout(1, 4));
		}

		initViewPorts();
		randomizeBaseLight();

		// Instantiate entities for every placeholder
		for (EntityPlaceholder placeholder : level.entityPlaceholders) {
			if (Rand.chance(placeholder.getChance())) {
				addEntity(entityFactory.build(placeholder.getType(), placeholder.getOrigin().cpy().scl(levelSpec.getTileset().tile_size)));
			}
		}

		setCurrentState(GameState.State.INGAME);

		addRenderEffect(FadeEffect.fadeIn(time()));

		// Add watches
		getPlayers().forEach(player -> {
			player.getConsole().watch("FPS", () -> Integer.toString(Gdx.graphics.getFramesPerSecond()));
//			GameState.console().watch("Time", () -> Float.toString(GameState.time()));
			player.getConsole().watch("Level", () -> Integer.toString(GameState.getLevelCount()));
			//TODO Fix
//			GameState.console().watch("Render calls", () -> Integer.toString(viewPortRenderer.getRenderCalls()));
//			GameState.console().watch("Frame time", () -> Float.toString(viewPortRenderer.getFrameTime()) + " ms");
		});
	}

	private static String getWelcomeMessage() {
		switch (levelCount) {
			case 1:
				return "Welcome to the dungeon";
			case 2:
				return "Well done";
			case 3:
				return "You're getting the vibe";
			case 4:
				return "Things will get harder now";
			case 5:
				return "Don't get too confident";
			case 6:
				return "This is as far as you get";
			default:
				return "Good luck. You'll need it";
		}
	}

	private static final double DEFAULT_SCALE = 3;

	/**
	 * Creates a viewport and viewport renderer for each player (in split screen fashion).
	 */
	public static void initViewPorts() {
		float scale = configuration.getDouble("viewport.scale", DEFAULT_SCALE).floatValue();

		Stack<ViewPort> viewPorts = new Stack<>();
		if (GameState.getPlayerCount() == 1) {
			viewPorts.push(new ViewPort(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scale));
		} else if (GameState.getPlayerCount() == 2) {
			scale -= 1;
			int width = Gdx.graphics.getWidth() / 2;
			int height = Gdx.graphics.getHeight();
			viewPorts.add(new ViewPort(width, 0, width, height, scale));
			viewPorts.add(new ViewPort(0, 0, width, height, scale));
		} else  {
			scale -= 2;
			int width = Gdx.graphics.getWidth() / 2;
			int height = Gdx.graphics.getHeight() / 2;
			viewPorts.add(new ViewPort(width, 0, width, height, scale));
			viewPorts.add(new ViewPort(0, 0, width, height, scale));
			viewPorts.add(new ViewPort(width, height, width, height, scale));
			viewPorts.add(new ViewPort(0, height, width, height, scale));
		}
		getPlayers().forEach(p -> {
			p.setViewPort(viewPorts.pop());
			p.setRenderer(new ViewPortRenderer(p.getViewPort(), p));
			p.getRenderer().initialize();
		});
	}

	public static Environment getLevelSpec() {
		return levelSpec;
	}

	public static void generateNewLevel() {
		int baseWidth = configuration.getLong("map.width", (long) BASE_MAP_WIDTH).intValue();
		int baseHeight = configuration.getLong("map.width", (long) BASE_MAP_HEIGHT).intValue();
		int growth = configuration.getLong("map.growth", (long) 10).intValue();

		levelSpec = Resources.environments.get("dungeon");
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(configuration, levelSpec, baseWidth + levelCount * growth, baseHeight + levelCount * growth);
		level = generator.generateLevel();
	}
	public static void addEntity(Entity entity) {
		newEntities.add(entity);
	}

	public static void addPlayerCharacter(PlayerEntity character) {
		newPlayerCharacters.add(character);
		newEntities.add(character);
	}

	public static void addOverlayText(OverlayText overlayText) {
		newOverelayTexts.add(overlayText);
	}

	public static List<Entity> getEntities() {
		return entities;
	}

	public static List<PlayerEntity> getPlayerCharacters() {
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
		List<Entity> spawning = new ArrayList<>(newEntities);
		newEntities.clear();
		spawning.forEach(Entity::spawn);
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

	public static Color getBaseLight() {
		return baseLight;
	}

	public static void randomizeBaseLight() {
		baseLight.set(Util.hsvaToColor(Rand.between(0f, 1f), 0.5f, 1f, 1f));
	}

	public static EntityType createLoot() {
		return Rand.pick(lootSet);
	}

}
