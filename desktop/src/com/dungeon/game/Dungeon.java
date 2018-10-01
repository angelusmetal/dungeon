package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.controller.ControllerConfig;
import com.dungeon.engine.controller.analog.DpadAnalogControl;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.effect.RenderEffect;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
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

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	}

	private void initResources() {
		Resources.load();
	}

	private void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleLighting));
		addDeveloperHotkey(Input.Keys.F2, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleScene));
		addDeveloperHotkey(Input.Keys.F3, Engine::randomizeBaseLight);
		addDeveloperHotkey(Input.Keys.F4, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleHealthbars));
		addDeveloperHotkey(Input.Keys.F5, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleBoundingBox));
		addDeveloperHotkey(Input.Keys.F6, () -> Players.all().stream().map(Player::getRenderer).forEach(ViewPortRenderer::toggleNoise));
	}

	private void addDeveloperHotkey(int keycode, Runnable runnable) {
		KeyboardToggle keyboardToggle = new KeyboardToggle(keycode);
		inputMultiplexer.addProcessor(keyboardToggle);
		Trigger trigger = new Trigger(keyboardToggle);
		trigger.addListener(runnable);
	}

	@Override
	public void render() {
		Engine.addTime(Gdx.graphics.getDeltaTime());

		for (Iterator<OverlayText> t = Engine.getOverlayTexts().iterator(); t.hasNext();) {
			OverlayText overlayText = t.next();
			overlayText.think();
			if (overlayText.isExpired()) {
				t.remove();
			}
		}

		// Render corresponding state
		if (Game.getCurrentState() == Game.State.MENU) {
			characterSelection.render();
		} else if (Game.getCurrentState() == Game.State.INGAME) {
			// Only process static entities in viewports
			// We need to pick up stuff that fits in all viewports & merge them with a set
			Stream<Entity> entitiesInAllViewPorts = Players.all().stream().flatMap(player -> Engine.entities.inViewPort(player.getViewPort(), 100f)).collect(Collectors.toSet()).stream();
			Engine.entities.update(entitiesInAllViewPorts);

			Players.all().forEach(player -> {
				characterViewPortTracker.refresh(player.getViewPort(), player.getAvatar());
				player.getRenderer().render();
			});
		}

		// Render effects on top
		Engine.getRenderEffects().forEach(RenderEffect::render);

		Engine.refresh();

		if (!fading && Game.getCurrentState() == Game.State.INGAME && Engine.entities.ofType(PlayerEntity.class).count() == 0) {
			fading = true;
			Engine.addRenderEffect(FadeEffect.fadeOutDeath(Engine.time(), () -> {
				Game.setCurrentState(Game.State.MENU);
				Engine.addRenderEffect(FadeEffect.fadeIn(Engine.time()));
				fading = false;
			}));
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
