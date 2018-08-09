package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.controller.ControllerConfig;
import com.dungeon.engine.controller.analog.DpadAnalogControl;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.player.ControllerPlayerControlBundle;
import com.dungeon.engine.controller.player.KeyboardPlayerControlBundle;
import com.dungeon.engine.controller.player.PlayerControlBundle;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.game.player.Player;
import com.dungeon.game.render.ViewPortRenderer;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.CharacterPlayerControlListener;
import com.dungeon.game.state.CharacterSelection;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;
import com.dungeon.game.state.SelectionPlayerControlListener;
import com.moandjiezana.toml.Toml;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Dungeon extends ApplicationAdapter {
	private final Toml configuration;
	private InputMultiplexer inputMultiplexer;
	private CharacterViewPortTracker characterViewPortTracker;
	private CharacterSelection characterSelection;

	private boolean fading = false;

	private long frame = 0;

	public Dungeon(Toml configuration) {
		this.configuration = configuration;
	}

	@Override
	public void create () {
		initResources();
		inputMultiplexer = new InputMultiplexer();
//		inputMultiplexer.addProcessor(viewPortInputProcessor);
//		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		Gdx.input.setInputProcessor(inputMultiplexer);

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

		GameState.initialize(configuration);

		characterSelection = new CharacterSelection();
		characterSelection.initialize();

		// Add keyboard controller
		PlayerControlBundle keyboardControl = new KeyboardPlayerControlBundle(inputMultiplexer);
		keyboardControl.addStateListener(GameState.State.INGAME, new CharacterPlayerControlListener(keyboardControl));
		keyboardControl.addStateListener(GameState.State.MENU, new SelectionPlayerControlListener(keyboardControl, characterSelection));

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			PlayerControlBundle controllerControl = new ControllerPlayerControlBundle(controller);
			controllerControl.addStateListener(GameState.State.INGAME, new CharacterPlayerControlListener(controllerControl));
			controllerControl.addStateListener(GameState.State.MENU, new SelectionPlayerControlListener(controllerControl, characterSelection));
		}

		// Add developer hotkeys
		addDeveloperHotkeys();

		characterViewPortTracker = new CharacterViewPortTracker();

	}

	private void initResources() {
		Resources.load();
	}

	private void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, () -> GameState.getPlayers().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleLighting));
		addDeveloperHotkey(Input.Keys.F2, () -> GameState.getPlayers().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleScene));
		addDeveloperHotkey(Input.Keys.F3, GameState::randomizeBaseLight);
		addDeveloperHotkey(Input.Keys.F4, () -> GameState.getPlayers().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleHealthbars));
		addDeveloperHotkey(Input.Keys.F5, () -> GameState.getPlayers().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleBoundingBox));
		addDeveloperHotkey(Input.Keys.F6, () -> GameState.getPlayers().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleNoise));
	}

	private void addDeveloperHotkey(int keycode, Runnable runnable) {
		KeyboardToggle keyboardToggle = new KeyboardToggle(keycode);
		inputMultiplexer.addProcessor(keyboardToggle);
		Trigger trigger = new Trigger(keyboardToggle);
		trigger.addListener(runnable);
	}

	@Override
	public void render() {
		GameState.addTime(Gdx.graphics.getDeltaTime());

		GameState.entities.process();

		for (Iterator<OverlayText> t = GameState.getOverlayTexts().iterator(); t.hasNext();) {
			OverlayText overlayText = t.next();
			overlayText.think();
			if (overlayText.isExpired()) {
				t.remove();
			}
		}

		// Render corresponding state
		if (GameState.getCurrentState() == GameState.State.MENU) {
			characterSelection.render();
		} else if (GameState.getCurrentState() == GameState.State.INGAME) {
			GameState.getPlayers().forEach(player -> {
				characterViewPortTracker.refresh(player.getViewPort(), player.getAvatar());
				player.getRenderer().render();
			});
		}

		// Render effects on top
		GameState.getRenderEffects().forEach(RenderEffect::render);

		GameState.refresh();

		if (!fading && GameState.getCurrentState() == GameState.State.INGAME && GameState.entities.playersAlive() == 0) {
			fading = true;
			GameState.addRenderEffect(FadeEffect.fadeOutDeath(GameState.time(), () -> {
				GameState.setCurrentState(GameState.State.MENU);
				GameState.addRenderEffect(FadeEffect.fadeIn(GameState.time()));
				fading = false;
			}));
		}

		this.frame += 1;
	}

	@Override
	public void dispose () {
		characterSelection.dispose();
		GameState.getPlayers().forEach(Disposable::dispose);
		Resources.dispose();
	}
}
