package com.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.console.CommandConsole;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.EntityFactory;
import com.dungeon.engine.entity.factory.EntityPrototypeFactory;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.level.generator.ForestLevelGenerator;
import com.dungeon.game.level.generator.LevelGenerator;
import com.dungeon.game.level.generator.ModularLevelGenerator;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.effect.FadeEffect;
import com.dungeon.game.render.stage.ViewPortRenderer;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.tileset.Environment;
import com.moandjiezana.toml.Toml;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	private static EntityFactory entityFactory;
	private static Toml configuration;
	private static List<String> lootSet;
	private static CommandConsole commandConsole = new CommandConsole();
	private static boolean displayConsole = false;

	public static OverlayText text(Vector2 origin, String text) {
		return text(origin, text, Color.WHITE);
	}

	public static OverlayText text(Vector2 origin, String text, Color color) {
		return new OverlayText(origin, text, color, Resources.fonts.get(Resources.DEFAULT_FONT));
	}

	private static Vector2 SAY_OFFSET = new Vector2(0, 20);

	public static OverlayText say(Entity emitter, String text) {
		OverlayText overlayText = text(emitter.getOrigin(), text)
				.spell(1, 0)
				.fadeout(1, 4)
				.bindTo(emitter, SAY_OFFSET);
		Engine.overlayTexts.add(overlayText);
		return overlayText;
	}

	public static OverlayText shout(Entity emitter, String text) {
		OverlayText overlayText = text(emitter.getOrigin(), text)
				.spell(0.25f, 0)
				.fadeout(1, 1)
				.bindTo(emitter, SAY_OFFSET);
		Engine.overlayTexts.add(overlayText);
		return overlayText;
	}

	private static Level level;
	private static Environment environment;
	private static State currentState = State.MENU;

	// List of level music...
	private static List<String> levelMusic = Arrays.asList(
			"audio/shy.ogg",
			"audio/roaming.ogg",
			"audio/street_dancing.wav",
			"audio/the_course.wav",
			"audio/the_crystal_chamber.wav",
			"audio/the_halls_of_nowhere.ogg"
	);

	public static void initialize(Toml configuration) {
		Game.configuration = configuration;
		entityFactory = new EntityFactory();
		initEntityFactories(entityFactory);
		lootSet = configuration.getList("map.items");
		Collections.shuffle(levelMusic);
	}
	private static void initEntityFactories(EntityFactory entityFactory) {
		Map<String, Object> factoryObjects = new HashMap<>();
		Resources.prototypes.getKeys().forEach(name -> {
			EntityPrototype prototype = Resources.prototypes.get(name);
			if (prototype.getFactory() != null) {
				String factoryName = prototype.getFactory();
				String className = factoryName.substring(0, factoryName.lastIndexOf('.'));
				String methodName = factoryName.substring(factoryName.lastIndexOf('.') + 1);
				Object factory = factoryObjects.computeIfAbsent(className, Game::createEntityFactory);
				try {
					MethodHandles.Lookup caller = MethodHandles.lookup();
					MethodType invokedType = MethodType.methodType(EntityPrototypeFactory.class, factory.getClass());
					MethodType methodType = MethodType.methodType(Entity.class, Vector2.class, EntityPrototype.class);
					MethodHandle handle = caller.findVirtual(factory.getClass(), methodName, methodType);
					EntityPrototypeFactory lambda = (EntityPrototypeFactory) LambdaMetafactory.metafactory(caller,
							"build",
							invokedType,
							methodType,
							handle,
							methodType)
							.getTarget()
							.bindTo(factory)
							.invoke();
					entityFactory.registerFactory(name, vector2 -> lambda.build(vector2, prototype));
				} catch (Throwable throwable) {
					throw new IllegalStateException(throwable);
				}
			} else {
				entityFactory.registerFactory(name, origin -> new DungeonEntity(prototype, origin));
			}
		});

	}

	private static Object createEntityFactory(String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public static float getDifficultyTier() {
		double playerMultiplier = Math.pow(1.5, Math.max(Players.count(), 1) - 1);
		double levelMultiplier = Math.pow(1.5, Math.max(getLevelCount(), 1) - 1);
		return (float) (playerMultiplier * levelMultiplier);
	}

	public static void startNewGame(List<Player> players) {
		Players.set(players);
		levelCount = 0;
		getCommandConsole().setOutput(Players.get(0).getConsole()::log);
		startNewLevel();
	}

	public static CommandConsole getCommandConsole() {
		return commandConsole;
	}

	public static boolean displayConsole() {
		return displayConsole;
	}

	public static void setDisplayConsole(boolean displayConsole) {
		Game.displayConsole = displayConsole;
	}

	public static void exitLevel() {
		Engine.renderEffects.add(FadeEffect.fadeOut(Engine.time(), Game::startNewLevel));
	}

	public static void startNewLevel() {

		generateNewLevel();

		Engine.entities.clear(level.getWidth() * level.getTileSize(), level.getHeight() * level.getTileSize());

		levelCount++;

		initViewPorts();

		// Get starting room and spawn player there
		List<EntityPlaceholder> playerSpawns = level.getEntityPlaceholders().stream().filter(ph -> ph.getType().equals(EntityType.PLAYER_SPAWN)).collect(Collectors.toList());
		int i = 0;
		for (Player player : Players.all()) {
			EntityPlaceholder spawnPoint = playerSpawns.get(i++);
			Vector2 origin = Util.floor(spawnPoint.getOrigin().cpy().scl(environment.getTileset().tile_size));
			player.spawn(origin);
			player.getRenderer().displayTitle("Chapter " + getLevelCount(), getWelcomeMessage());
		}

		// Instantiate entities for every placeholder
		level.getEntityPlaceholders().stream().filter(ph -> !ph.getType().equals(EntityType.PLAYER_SPAWN)).forEach(placeholder -> {
			if (Rand.chance(placeholder.getChance())) {
				Engine.entities.add(entityFactory.build(placeholder.getType(), Util.floor(placeholder.getOrigin().cpy().scl(environment.getTileset().tile_size))));
			}
		});

		Engine.entities.commit(false);
		System.out.println(Engine.entities.analysis());

		setCurrentState(State.INGAME);

		Engine.renderEffects.add(FadeEffect.fadeIn(Engine.time()));

		// Start playing new music
		Engine.audio.playMusic(Gdx.files.internal(levelMusic.get((levelCount - 1) % levelMusic.size())));

		// Add watches
		Players.all().forEach(player -> {
			player.getConsole().watch("FPS", () -> Integer.toString(Gdx.graphics.getFramesPerSecond()));
//			player.getConsole().watch("Time", () -> Float.toString(Engine.time()));
//			player.getConsole().watch("Level", () -> Integer.toString(Game.getLevelCount()));
//			player.getConsole().watch("Origin", () -> player.getAvatar().getOrigin().toString());
//			player.getConsole().watch("Static Entities", () -> Integer.toString(Engine.entities.staticCount()));
//			player.getConsole().watch("Dynamic Entities", () -> Integer.toString(Engine.entities.dynamicCount()));
//			player.getConsole().watch("Entity updateAll", () -> Engine.entities.processTime.toString());
//			player.getConsole().watch("Mov", () -> player.getAvatar().getMovement().toString());
//			player.getConsole().watch("Aim", () -> player.getAvatar().getAim().toString());
//			player.getConsole().watch("QuadTree", () -> Engine.entities.analysis());
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

		LinkedList<ViewPort> viewPorts = new LinkedList<>();
		if (Players.count() == 1) {
			viewPorts.push(new ViewPort(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scale));
		} else if (Players.count() == 2) {
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
		Players.all().forEach(p -> {
			p.setViewPort(viewPorts.pop());
			p.setRenderer(new ViewPortRenderer(p.getViewPort(), p));
			p.getRenderer().initialize();
		});
		Engine.setMainViewport(Players.get(0).getViewPort());
	}

	public static Environment getEnvironment() {
		return environment;
	}

	public static void generateNewLevel() {
		// Pick a random environment
		String env = Rand.pick(Resources.environments.getKeys());
		env = "dungeon";
//		env = "prairie";
		environment = Resources.environments.get(env);
		Engine.setBaseLight(environment.getLight().get());
		LevelGenerator generator;
		// TODO Wire the corresponding generator in the environment definition
		if (env.equals("dungeon")) {
//			generator = new ModularLevelGenerator(environment, baseWidth + levelCount * growth, baseHeight + levelCount * growth);
			generator = new ModularLevelGenerator(environment, 200, 200);
		} else {
//			generator = new ForestLevelGenerator(environment, 50, 50, 4d);
			generator = new ForestLevelGenerator(environment, 500, 500, 4d);
		}
		level = generator.generateLevel();
		Engine.setLevelTiles(level);
	}

	public static String createLoot() {
		return Rand.pick(lootSet);
	}

	public static void createCreatureLoot(Vector2 origin) {
		if (Rand.chance(1f)) { // Always
			Rand.doBetween(1, 3, () -> {
				Entity coin = build(EntityType.COIN, origin);
				coin.impulse(Rand.between(-20, 20), Rand.between(-20, 20));
				coin.setZSpeed(Rand.between(50, 150));
				Engine.entities.add(coin);
			});
		}
	}

	public static Level getLevel() {
		return level;
	}

	public static State getCurrentState() {
		return currentState;
	}
	public static void setCurrentState(State newState) {
		currentState = newState;
	}

	public static Toml getConfiguration() {
		return configuration;
	}

	public static Entity build(String type, Vector2 position) {
		return entityFactory.build(type, position);
	}

	private static int levelCount;

	public static int getLevelCount() {
		return levelCount;
	}
}
