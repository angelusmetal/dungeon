package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.controller.ControllerConfig;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.StopWatch;
import com.dungeon.game.controller.ControlBundle;
import com.dungeon.game.controller.ControllerControlBundle;
import com.dungeon.game.controller.KeyboardControlBundle;
import com.dungeon.game.developer.DevCommands;
import com.dungeon.game.developer.DevTools;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.effect.FadeEffect;
import com.dungeon.game.resource.DungeonResources;
import com.dungeon.game.state.CharacterPlayerControlListener;
import com.dungeon.game.state.CharacterSelection;
import com.dungeon.game.state.SelectionPlayerControlListener;
import com.moandjiezana.toml.Toml;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dungeon.game.developer.DevTools.*;
import static com.dungeon.game.render.stage.TransitionStage.LEVEL_TRANSITION_TIME;

public class Dungeon extends ApplicationAdapter {

	private StopWatch stopWatch = new StopWatch();

	private final Toml configuration;
	private CharacterSelection characterSelection;

	private boolean fading = false;

	private long frame = 0;
	private DevCommands devCommands;

	public Dungeon(Toml configuration) {
		this.configuration = configuration;
	}

	@Override
	public void create() {
		initResources();
		Game.devTools = new DevTools(Engine.inputMultiplexer);
		devCommands = new DevCommands(Game.devTools);

		// Set F12 to push & pop console input from the input processor
		Game.devTools.addDeveloperHotkey(Input.Keys.ENTER, () -> {
			Game.setDisplayConsole(true);
			Engine.inputStack.push(Engine.console.getInputProcessor());
		});
		Engine.console.bindKey(Input.Keys.ENTER, () -> {
			Engine.console.commandExecute();
			Game.setDisplayConsole(false);
			Engine.inputStack.pop();
		});

		Game.initialize(configuration);

		characterSelection = new CharacterSelection();
		characterSelection.initialize();

		configureInput();

		// Add developer hotkeys
		Game.devTools.addDeveloperHotkeys();

		// Start playing character selection music
		Engine.audio.playMusic(Gdx.files.internal("audio/character_select.mp3"), 0f);

		System.err.println("GL_MAX_TEXTURE_SIZE: " + Engine.getMaxTextureSize());
	}

	private void configureInput() {
		Map<String, ControllerConfig> controllerConfigs = readControllerConfigurations();

		// Add keyboard controller
		ControlBundle keyboardControl = new KeyboardControlBundle(Engine.inputMultiplexer);
		keyboardControl.addStateListener(Game.State.INGAME, new CharacterPlayerControlListener(keyboardControl));
		keyboardControl.addStateListener(Game.State.MENU, new SelectionPlayerControlListener(keyboardControl, characterSelection));

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			System.out.println("Detected controller '" + controller.getName() + (controllerConfigs.containsKey(controller.getName()) ? "' and found a configuration for it" : "' but found no configuration for it"));
			ControlBundle controllerControl = new ControllerControlBundle(controller, controllerConfigs.get(controller.getName()));
			controllerControl.addStateListener(Game.State.INGAME, new CharacterPlayerControlListener(controllerControl));
			controllerControl.addStateListener(Game.State.MENU, new SelectionPlayerControlListener(controllerControl, characterSelection));
		}
	}

	private Map<String, ControllerConfig> readControllerConfigurations() {
		Map<String, ControllerConfig> configurations = new HashMap<>();
		Config config = ConfigFactory.load("config.conf");
		if (config.hasPath("controller")) {
			Config controllerConfig = config.getConfig("controller");
			for (Map.Entry<String, ConfigValue> entry : controllerConfig.root().entrySet()) {
				Config current = controllerConfig.getConfig(entry.getKey());
				ControllerConfig.Builder builder = new ControllerConfig.Builder();
				ConfigUtil.getInteger(current, "povControl").map(builder::povControl);
				ConfigUtil.getInteger(current, "moveAxisX").map(builder::moveAxisX);
				ConfigUtil.getBoolean(current, "moveAxisXInvert").map(builder::moveAxisXInvert);
				ConfigUtil.getInteger(current, "moveAxisY").map(builder::moveAxisY);
				ConfigUtil.getBoolean(current, "moveAxisYInvert").map(builder::moveAxisYInvert);
				ConfigUtil.getInteger(current, "aimAxisX").map(builder::aimAxisX);
				ConfigUtil.getBoolean(current, "aimAxisXInvert").map(builder::aimAxisXInvert);
				ConfigUtil.getInteger(current, "aimAxisY").map(builder::aimAxisY);
				ConfigUtil.getBoolean(current, "aimAxisYInvert").map(builder::aimAxisYInvert);
				ConfigUtil.getInteger(current, "buttonA").map(builder::buttonA);
				ConfigUtil.getInteger(current, "buttonB").map(builder::buttonB);
				ConfigUtil.getInteger(current, "buttonX").map(builder::buttonX);
				ConfigUtil.getInteger(current, "buttonY").map(builder::buttonY);
				ConfigUtil.getInteger(current, "buttonL1").map(builder::buttonL1);
				ConfigUtil.getInteger(current, "buttonL2").map(builder::buttonL2);
				ConfigUtil.getInteger(current, "buttonR1").map(builder::buttonR1);
				ConfigUtil.getInteger(current, "buttonR2").map(builder::buttonR2);
				configurations.put(entry.getKey(), builder.build());
			}
		}
		return configurations;
	}

	private void initResources() {
		DungeonResources.addLoaders();
		Resources.loader.load("assets/assets.conf");
	}

	@Override
	public void render() {
		stopWatch.start();
		Engine.addTime(Gdx.graphics.getDeltaTime());
		Engine.overlayTexts.update(OverlayText::think, OverlayText::isExpired, o -> {});

		// Render corresponding state
		if (Game.getCurrentState() == Game.State.MENU) {
			characterSelection.render();
		} else if (Game.getCurrentState() == Game.State.INGAME) {
			// Only process static entities in viewports
			// We need to pick up stuff that fits in all viewports & merge them with a set
			Stream<Entity> entitiesInAllViewPorts = Players.all().stream().flatMap(player -> Engine.entities.inViewPort(player.getViewPort(), 100f)).collect(Collectors.toSet()).stream();
			Engine.entities.update(entitiesInAllViewPorts);
			entitiesSampler.sample((int) stopWatch.getAndReset());

			Game.gameView.updateCamera();
			Game.gameView.render();
			renderSampler.sample((int) stopWatch.getAndReset());
		}

		// Render effects on top
		Engine.renderEffects.update(RenderEffect::render, RenderEffect::isExpired, RenderEffect::dispose);

		Engine.refresh();

		if (!fading && Game.getCurrentState() == Game.State.INGAME && Engine.entities.ofType(PlayerEntity.class).count() == 0) {
			fading = true;
			Engine.audio.fadeOut(LEVEL_TRANSITION_TIME);
			Players.all().stream().map(Player::getRenderer).forEach(renderer -> renderer.closeTransition(LEVEL_TRANSITION_TIME, () -> {
				Game.setCurrentState(Game.State.MENU);
				Engine.renderEffects.add(FadeEffect.fadeIn(Engine.time()));
				fading = false;
				// Start playing character selection music
 				Engine.audio.playMusic(Gdx.files.internal("audio/character_select.mp3"), 0f);
			}));
		}

		Game.devTools.draw();

		if (Players.count() > 0) {
			movementSampler.sample((int) Players.get(0).getAvatar().getMovement().len());
		}

		Game.runScheduledUpdates();

		this.frame++;
	}

	@Override
	public void dispose () {
		characterSelection.dispose();
		Game.gameView.dispose();
		Resources.dispose();
	}


}
