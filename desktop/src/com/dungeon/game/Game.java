package com.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.EntityFactory;
import com.dungeon.engine.entity.factory.EntityPrototypeFactory;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Game {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	private static EntityFactory entityFactory;
	private static Toml configuration;
	private static List<String> lootSet;

	public static OverlayText text(Vector2 origin, String text) {
		return text(origin, text, Color.WHITE);
	}

	public static OverlayText text(Vector2 origin, String text, Color color) {
		return new OverlayText(origin, text, color, Resources.fonts.get(Resources.DEFAULT_FONT));
	}
	private static Level level;
	private static Environment environment;
	private static State currentState = State.MENU;

	public static void initialize(Toml configuration) {
		entityFactory = new EntityFactory();
		Game.configuration = configuration;
		lootSet = configuration.getList("map.items");
		initEntityFactories(entityFactory);
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
				entityFactory.registerFactory(name, origin -> new Entity(prototype, origin));
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
		startNewLevel();
	}

	public static void exitLevel() {
		Engine.addRenderEffect(FadeEffect.fadeOut(Engine.time(), Game::startNewLevel));
	}

	public static void startNewLevel() {

		Engine.entities.clear();

		generateNewLevel();

		// Update player count
//		playerCount = players.size();
		levelCount++;

		// Get starting room and spawn player there
		Room startingRoom = level.rooms.get(0);
		int spawnPoint = 0;
		for (Player player : Players.all()) {
			Vector2 origin = startingRoom.spawnPoints.get(spawnPoint++).cpy().scl(environment.getTileset().tile_size);
			player.spawn(origin);
			origin.y += 30;
			Engine.addOverlayText(text(origin, getWelcomeMessage()).spell(1, 1).fadeout(1, 4));
		}

		initViewPorts();
//		randomizeBaseLight();

		// Instantiate entities for every placeholder
		for (EntityPlaceholder placeholder : level.entityPlaceholders) {
			if (Rand.chance(placeholder.getChance())) {
				Engine.entities.add(entityFactory.build(placeholder.getType(), placeholder.getOrigin().cpy().scl(environment.getTileset().tile_size)));
			}
		}

		setCurrentState(State.INGAME);

		Engine.addRenderEffect(FadeEffect.fadeIn(Engine.time()));

		// Add watches
		Players.all().forEach(player -> {
			player.getConsole().watch("FPS", () -> Integer.toString(Gdx.graphics.getFramesPerSecond()));
//			GameState.console().watch("Time", () -> Float.toString(GameState.time()));
			player.getConsole().watch("Level", () -> Integer.toString(Game.getLevelCount()));
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
	}

	public static Environment getEnvironment() {
		return environment;
	}

	public static void generateNewLevel() {
		int baseWidth = configuration.getLong("map.width", 40L).intValue();
		int baseHeight = configuration.getLong("map.width", 40L).intValue();
		int growth = configuration.getLong("map.growth", (long) 10).intValue();

		// Pick a random environment
		String env = Rand.pick(Resources.environments.getKeys());
		env = "dungeon";
		environment = Resources.environments.get(env);
		Engine.setBaseLight(environment.getLight().get());
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(configuration, environment, baseWidth + levelCount * growth, baseHeight + levelCount * growth);
		level = generator.generateLevel();
		Engine.setLevelTiles(level);
	}

	public static String createLoot() {
		return Rand.pick(lootSet);
	}

	public static void createCreatureLoot(Vector2 origin) {
		if (Rand.chance(0.5f)) {
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
