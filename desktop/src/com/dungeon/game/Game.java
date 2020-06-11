package com.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.console.Console;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.entity.factory.EntityFactory;
import com.dungeon.engine.entity.factory.EntityPrototypeFactory;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.developer.DevTools;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.level.generator.ForestLevelGenerator;
import com.dungeon.game.level.generator.LevelGenerator;
import com.dungeon.game.level.generator.ModularLevelGenerator;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.stage.SceneStage;
import com.dungeon.game.resource.DungeonResources;
import com.dungeon.game.resource.loader.LootGenerator;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.viewport.GameView;
import com.dungeon.game.viewport.SharedScreenCreationStrategy;
import com.dungeon.game.viewport.SplitScreenCreationStrategy;
import com.moandjiezana.toml.Toml;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dungeon.game.render.stage.TransitionStage.LEVEL_TRANSITION_TIME;

public class Game {

	public enum State {
		MENU, INGAME, GAMEOVER
	}

	private static EntityFactory entityFactory;
	private static Toml configuration;
	private static List<String> lootSet;
	private static Console console = new Console();
	private static boolean displayConsole = false;
	public static GameView gameView = new GameView();
	public static DevTools devTools;
	public static List<Runnable> updates = new ArrayList<>();

	public static OverlayText text(Vector2 origin, String text) {
		return text(origin, text, Color.WHITE);
	}

	public static OverlayText text(Vector2 origin, String text, Color color) {
		return new OverlayText(origin, text, color, Resources.fonts.get(DungeonResources.DEFAULT_FONT));
	}

	private static Vector2 SAY_OFFSET = new Vector2(0, 40);

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
		Config config = ConfigFactory.load("config.conf");
		Game.configuration = configuration;
		entityFactory = new EntityFactory();
		initEntityFactories(entityFactory);
		Collections.shuffle(levelMusic);

		float scale = ConfigUtil.getFloat(config, "viewport.scale").orElse(DEFAULT_SCALE);
		float margin = ConfigUtil.getFloat(config, "viewport.margin").orElse(DEFAULT_MARGIN);
		String strategy = ConfigUtil.getString(config, "viewport.strategy").orElse("split");
		if ("split".equals(strategy)) {
			gameView.setCreationStrategy(new SplitScreenCreationStrategy(scale));
		} else {
			gameView.setCreationStrategy(new SharedScreenCreationStrategy(scale, margin));
		}
	}
	private static void initEntityFactories(EntityFactory entityFactory) {
		Map<String, Object> factoryObjects = new HashMap<>();
		DungeonResources.prototypes.getKeys().forEach(name -> {
			EntityPrototype prototype = DungeonResources.prototypes.get(name);
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
		getConsole().setOutput(Players.get(0).getConsole()::log);
		startNewLevel();
	}

	public static Console getConsole() {
		return console;
	}

	public static boolean displayConsole() {
		return displayConsole;
	}

	public static void setDisplayConsole(boolean displayConsole) {
		Game.displayConsole = displayConsole;
	}

	public static void exitLevel() {
		// Fade out music
		Engine.audio.fadeOut(LEVEL_TRANSITION_TIME);
		// Disable movement
		Players.all().forEach(Player::disableAvatarControl);
		// Fire up transition effect
		Players.all().stream().map(Player::getRenderer).forEach(renderer -> renderer.closeTransition(LEVEL_TRANSITION_TIME, Game::startNewLevel));
	}

	public static void startNewLevel() {

		generateNewLevel();

		Engine.entities.clear(level.getWidth() * level.getTileSize(), level.getHeight() * level.getTileSize());

		levelCount++;

		// Get starting room and spawn player there
		List<EntityPlaceholder> playerSpawns = level.getEntityPlaceholders().stream().filter(ph -> ph.getType().equals(EntityType.PLAYER_SPAWN)).collect(Collectors.toList());
		int i = 0;
		for (Player player : Players.all()) {
			EntityPlaceholder spawnPoint = playerSpawns.get(i++);
			Vector2 origin = Util.floor(spawnPoint.getOrigin().cpy().scl(environment.getTilesize()));
			player.spawn(origin);
		}

		// Instantiate entities for every placeholder
		level.getEntityPlaceholders().stream().filter(ph -> !ph.getType().equals(EntityType.PLAYER_SPAWN)).forEach(placeholder -> {
			if (Rand.chance(placeholder.getChance())) {
				if (EntityType.LIGHT.equals(placeholder.getType())) {
					// Light placeholder (which inlines light definition)
					Entity light = entityFactory.build(EntityType.LIGHT, Util.floor(placeholder.getOrigin().cpy().scl(environment.getTilesize())));
					light.setLight(new Light(placeholder.getLightPrototype()));
					Engine.entities.add(light);
				} else {
					// Regular placeholder
					Engine.entities.add(entityFactory.build(placeholder.getType(), Util.floor(placeholder.getOrigin().cpy().scl(environment.getTilesize()))));
				}
			}
		});

		gameView.recreateViewPorts();
		Players.all().forEach(player -> player.getRenderer().displayTitle("Chapter " + getLevelCount(), getWelcomeMessage()));

		Engine.entities.commit(false);
		System.out.println(Engine.entities.analysis());

		setCurrentState(State.INGAME);

		// Open transition
		Players.all().stream().map(Player::getRenderer).forEach(renderer -> renderer.openTransition(LEVEL_TRANSITION_TIME, () -> {}));

		// Start playing new music
		Engine.audio.playMusic(Gdx.files.internal(levelMusic.get((levelCount - 1) % levelMusic.size())), 0f);

		// Add watches
		Players.all().forEach(player -> {
			player.getConsole().watch("FPS", () -> Integer.toString(Gdx.graphics.getFramesPerSecond()));
			player.getConsole().watch("Lights", () -> Integer.toString(SceneStage.lightCount));
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

	private static final float DEFAULT_SCALE = 3;
	private static final float DEFAULT_MARGIN = 100;

	public static Environment getEnvironment() {
		return environment;
	}

	public static void generateNewLevel() {
		// Pick a random environment
		String env = Rand.pick(DungeonResources.environments.getKeys());
		env = "dungeon";
//		env = "prairie";
		environment = DungeonResources.environments.get(env);
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

	public static Stream<String> knownEntityTypes() {
		return entityFactory.knownTypes();
	}

	private static int levelCount;

	public static int getLevelCount() {
		return levelCount;
	}

	public static void setLevelCount(int levelCount) {
		Game.levelCount = levelCount;
	}

	/**
	 * Schedule an update to be executed at the end of the current cycle
	 */
	public static void scheduleUpdate(Runnable runnable) {
		updates.add(runnable);
	}

	/**
	 * Run all scheduled updates
	 */
	public static void runScheduledUpdates() {
		updates.forEach(Runnable::run);
		updates.clear();
	}
}
