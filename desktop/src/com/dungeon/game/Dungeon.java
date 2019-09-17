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

public class Dungeon extends ApplicationAdapter {

	public static CyclicSampler movementSampler = new CyclicSampler(200);
	public static CyclicSampler entitiesSampler = new CyclicSampler(200);
	public static CyclicSampler renderSampler = new CyclicSampler(200);
	public static CyclicSampler sceneSampler = new CyclicSampler(200);
	public static CyclicSampler healthbarSampler = new CyclicSampler(200);
	public static CyclicSampler collisionSampler = new CyclicSampler(200);
	public static CyclicSampler noiseSampler = new CyclicSampler(200);
	public static CyclicSampler motionBlurSampler = new CyclicSampler(200);
	public static CyclicSampler overlayTextSampler = new CyclicSampler(200);
	public static CyclicSampler playerArrowsSampler = new CyclicSampler(200);
	public static CyclicSampler hudSampler = new CyclicSampler(200);
	public static CyclicSampler miniMapSampler = new CyclicSampler(200);
	public static CyclicSampler titleSampler = new CyclicSampler(200);
	public static CyclicSampler scaleSampler = new CyclicSampler(200);
	public static CyclicSampler consoleSampler = new CyclicSampler(200);
	public static CyclicSampler profilerSampler = new CyclicSampler(200);

	private StopWatch stopWatch = new StopWatch();
	private VLayout profilerWidget = new VLayout();

	private final Toml configuration;
	private InputMultiplexer inputMultiplexer;
	private InputProcessorStack inputStack;
	private CharacterViewPortTracker characterViewPortTracker;
	private CharacterSelection characterSelection;

	private boolean fading = false;

	private long frame = 0;
	private boolean drawProfiler = false;
	private Vector2 mouseOrigin = new Vector2();

	public Dungeon(Toml configuration) {
		this.configuration = configuration;
	}

	@Override
	public void create () {
		initResources();
		inputStack = new InputProcessorStack();
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new AbstractInputProcessor() {
			@Override public boolean mouseMoved(int screenX, int screenY) {
				mouseOrigin.set(screenX, Gdx.graphics.getHeight() - screenY);
				return true;
			}
		});
		inputStack.push(inputMultiplexer);
//		inputMultiplexer.addProcessor(viewPortInputProcessor);
//		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		Gdx.input.setInputProcessor(inputStack);

		// Set F12 to push & pop console input from the input processor
		addDeveloperHotkey(Input.Keys.ENTER, () -> {
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
		addDeveloperHotkeys();

		characterViewPortTracker = new CharacterViewPortTracker();

		// Add visual profiler stuff
		profilerWidget.add(
				new SamplerVisualizer(entitiesSampler, "upd")
						.color(new Color(0f, 0f, 1f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(renderSampler, "ren")
						.color(new Color(1f, 0f, 0f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(movementSampler, "mov")
						.color(new Color(0f, 1f, 0f, 0.5f))
						.formatter(String::valueOf));
		profilerWidget.add(
				new SamplerVisualizer(sceneSampler, "scene")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(healthbarSampler, "healthbar")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(collisionSampler, "collision")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(noiseSampler, "noise")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(motionBlurSampler, "motionBlur")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(overlayTextSampler, "overlayText")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(playerArrowsSampler, "playerArrow")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(hudSampler, "hud")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(miniMapSampler, "miniMap")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(titleSampler, "title")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(scaleSampler, "scale")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(consoleSampler, "console")
						.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.add(
				new SamplerVisualizer(profilerSampler, "profiler")
						.color(new Color(0.4f, 0.4f, 0.4f, 0.5f))
						.formatter(Util::nanosToString));
		profilerWidget.setX(Gdx.graphics.getWidth() - 150);
		profilerWidget.setY(0);

		// Start playing character selection music
		Engine.audio.playMusic(Gdx.files.internal("audio/character_select.mp3"));
	}

	private void initResources() {
		Resources.load();
	}

	private void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleTiles));
		addDeveloperHotkey(Input.Keys.F2, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleEntities));
		addDeveloperHotkey(Input.Keys.F3, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleLighting));
		addDeveloperHotkey(Input.Keys.F4, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleShadows));
		addDeveloperHotkey(Input.Keys.F5, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleBoundingBox));
		addDeveloperHotkey(Input.Keys.F6, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleNoise));
		addDeveloperHotkey(Input.Keys.F7, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleConsole));
		addDeveloperHotkey(Input.Keys.F11, () -> drawProfiler = !drawProfiler);

		// Add console commands
		Game.getCommandConsole().bindCommand("play_music", this::playMusicCommand);
		Game.getCommandConsole().bindCommand("spawn", this::spawnCommand);
	}

	private void playMusicCommand(List<String> tokens) {
		String path = tokens.get(1);
		Engine.audio.playMusic(Gdx.files.internal(path));
	}
	private void spawnCommand(List<String> tokens) {
		String type = tokens.get(1);
		try {
			Engine.entities.add(Game.build(type, mouseAt()));
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
	}

	private Vector2 mouseAt() {
		ViewPort viewPort = Players.get(0).getViewPort();
		return mouseOrigin.cpy().scl(1 / viewPort.getScale()).add(viewPort.cameraX, viewPort.cameraY);
	}

	private void addDeveloperHotkey(int keycode, Runnable runnable) {
		KeyboardToggle keyboardToggle = new KeyboardToggle(keycode);
		inputMultiplexer.addProcessor(keyboardToggle);
		Trigger trigger = new Trigger(keyboardToggle);
		trigger.addListener(runnable);
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

		if (drawProfiler) {
			stopWatch.start();
			SpriteBatch batch = new SpriteBatch();
			batch.begin();
			profilerWidget.draw(batch);
			batch.end();
			profilerSampler.sample((int) stopWatch.getAndReset());
		}

		if (Players.count() > 0) {
			movementSampler.sample((int) Players.get(0).getAvatar().getMovement().len());
		}

		this.frame += 1;
	}

	@Override
	public void dispose () {
		characterSelection.dispose();
		Players.all().forEach(Disposable::dispose);
		Resources.dispose();
	}


}
