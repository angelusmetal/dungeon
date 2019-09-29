package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.console.AbstractInputProcessor;
import com.dungeon.engine.console.InputProcessorStack;
import com.dungeon.engine.controller.ControllerConfig;
import com.dungeon.engine.controller.analog.DpadAnalogControl;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.factory.EntityFactory;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.ui.widget.SamplerVisualizer;
import com.dungeon.engine.ui.widget.VLayout;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.CyclicSampler;
import com.dungeon.engine.util.StopWatch;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.controller.ControllerPlayerControlBundle;
import com.dungeon.game.controller.KeyboardPlayerControlBundle;
import com.dungeon.game.controller.PlayerControlBundle;
import com.dungeon.game.developer.DevCommands;
import com.dungeon.game.developer.DevTools;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.render.effect.FadeEffect;
import com.dungeon.game.render.stage.ViewPortRenderer;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.CharacterPlayerControlListener;
import com.dungeon.game.state.CharacterSelection;
import com.dungeon.game.state.SelectionPlayerControlListener;
import com.moandjiezana.toml.Toml;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dungeon.game.developer.DevTools.*;

public class Dungeon extends ApplicationAdapter {

	private StopWatch stopWatch = new StopWatch();

	private final Toml configuration;
	private InputMultiplexer inputMultiplexer;
	private InputProcessorStack inputStack;
	private CharacterViewPortTracker characterViewPortTracker;
	private CharacterSelection characterSelection;

	private boolean fading = false;

	private long frame = 0;
	private DevTools devTools;
	private DevCommands devCommands;

	public Dungeon(Toml configuration) {
		this.configuration = configuration;
	}

	@Override
	public void create () {
		initResources();
		inputStack = new InputProcessorStack();
		inputMultiplexer = new InputMultiplexer();
		devTools = new DevTools(inputMultiplexer);
		devCommands = new DevCommands(devTools);
		inputStack.push(inputMultiplexer);
		Gdx.input.setInputProcessor(inputStack);

		// Set F12 to push & pop console input from the input processor
		devTools.addDeveloperHotkey(Input.Keys.ENTER, () -> {
			Game.setDisplayConsole(true);
			inputStack.push(Game.getCommandConsole().getInputProcessor());
		});
		Game.getCommandConsole().bindKey(Input.Keys.ENTER, () -> {
			Game.getCommandConsole().commandExecute();
			Game.setDisplayConsole(false);
			inputStack.pop();
		});

		Map<String, ControllerConfig> controllerConfigs = ConfigUtil.getListOf(configuration, "controllers", ControllerConfig.class)
				.stream()
				.collect(Collectors.toMap(ControllerConfig::getId, Function.identity()));

		for (Controller controller : Controllers.getControllers()) {
			System.out.println("Found controller: " + controller.getName());
			ControllerConfig controllerConfig = controllerConfigs.getOrDefault(controller.getName(), ControllerConfig.DEFAULT);
			DpadAnalogControl povControl = new DpadAnalogControl(controllerConfig.povControl);
			StickAnalogControl analogControl = new StickAnalogControl(controllerConfig.analogControlX, controllerConfig.analogControlY);
			controller.addListener(povControl);
			controller.addListener(analogControl);
		}

		Game.initialize(configuration);

		characterSelection = new CharacterSelection();
		characterSelection.initialize();

		// Add keyboard controller
		PlayerControlBundle keyboardControl = new KeyboardPlayerControlBundle(inputMultiplexer);
		keyboardControl.addStateListener(Game.State.INGAME, new CharacterPlayerControlListener(keyboardControl));
		keyboardControl.addStateListener(Game.State.MENU, new SelectionPlayerControlListener(keyboardControl, characterSelection));

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			PlayerControlBundle controllerControl = new ControllerPlayerControlBundle(controller);
			controllerControl.addStateListener(Game.State.INGAME, new CharacterPlayerControlListener(controllerControl));
			controllerControl.addStateListener(Game.State.MENU, new SelectionPlayerControlListener(controllerControl, characterSelection));
		}

		// Add developer hotkeys
		devTools.addDeveloperHotkeys();
		devTools.addProfilerWidgets();

		characterViewPortTracker = new CharacterViewPortTracker();

		// Start playing character selection music
		Engine.audio.playMusic(Gdx.files.internal("audio/character_select.mp3"));
	}

	private void initResources() {
		Resources.load();
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

			Players.all().forEach(player -> {
				characterViewPortTracker.refresh(player.getViewPort(), player.getAvatar());
				player.getRenderer().render();
			});
			renderSampler.sample((int) stopWatch.getAndReset());
		}

		// Render effects on top
		Engine.renderEffects.update(RenderEffect::render, RenderEffect::isExpired, RenderEffect::dispose);

		Engine.refresh();

		if (!fading && Game.getCurrentState() == Game.State.INGAME && Engine.entities.ofType(PlayerEntity.class).count() == 0) {
			fading = true;
			Engine.renderEffects.add(FadeEffect.fadeOutDeath(Engine.time(), () -> {
				Game.setCurrentState(Game.State.MENU);
				Engine.renderEffects.add(FadeEffect.fadeIn(Engine.time()));
				fading = false;
				// Start playing character selection music
 				Engine.audio.playMusic(Gdx.files.internal("audio/character_select.mp3"));
			}));
		}

		devTools.draw();

		if (Players.count() > 0) {
			movementSampler.sample((int) Players.get(0).getAvatar().getMovement().len());
		}

		this.frame++;
	}

	@Override
	public void dispose () {
		characterSelection.dispose();
		Players.all().forEach(Disposable::dispose);
		Resources.dispose();
	}


}
