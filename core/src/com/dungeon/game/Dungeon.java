package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.input.GestureDetector;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.analog.DpadAnalogControl;
import com.dungeon.engine.controller.player.ControllerPlayerControlBundle;
import com.dungeon.engine.controller.player.KeyboardPlayerControlBundle;
import com.dungeon.engine.controller.player.PlayerControlBundle;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.viewport.ViewPortInputProcessor;
import com.dungeon.game.character.Ghost;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.object.HealthPowerup;
import com.dungeon.game.object.Torch;
import com.dungeon.game.state.CharacterPlayerControlListener;
import com.dungeon.game.state.CharacterSelection;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.SelectionPlayerControlListener;

import java.util.Iterator;

public class Dungeon extends ApplicationAdapter {
	public static final float INITIAL_SCALE = 4;
	private GameState state;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private CharacterViewPortTracker characterViewPortTracker;
	private IngameRenderer ingameRenderer;
	private CharacterSelection characterSelection;
	private EntityFactory entityFactory;

	long frame = 0;

	@Override
	public void create () {
		viewPort = new ViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, INITIAL_SCALE);
		viewPortInputProcessor = new ViewPortInputProcessor(viewPort);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(viewPortInputProcessor);
		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		Gdx.input.setInputProcessor(inputMultiplexer);

		for (Controller controller : Controllers.getControllers()) {
			DpadAnalogControl povControl = new DpadAnalogControl(0);
			StickAnalogControl analogControl = new StickAnalogControl(3, -2);
			controller.addListener(povControl);
			controller.addListener(analogControl);
		}

		entityFactory = new EntityFactory();
		state = new GameState(viewPort, entityFactory);

		entityFactory.registerFactory(EntityType.GHOST, new Ghost.Factory(state));
		entityFactory.registerFactory(EntityType.TORCH, new Torch.Factory(state));
		entityFactory.registerFactory(EntityType.HEALTH_POWERUP, new HealthPowerup.Factory(state));

		Light.initialize();
		ingameRenderer = new IngameRenderer(state, viewPort);
		ingameRenderer.initialize();
		characterSelection = new CharacterSelection(state);
		characterSelection.initialize();

		// Add keyboard controller
		PlayerControlBundle keyboardControl = new KeyboardPlayerControlBundle(state, inputMultiplexer);
		keyboardControl.addStateListener(GameState.State.INGAME, new CharacterPlayerControlListener(keyboardControl, state));
		keyboardControl.addStateListener(GameState.State.MENU, new SelectionPlayerControlListener(keyboardControl, characterSelection));

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			PlayerControlBundle controllerControl = new ControllerPlayerControlBundle(state, controller);
			controllerControl.addStateListener(GameState.State.INGAME, new CharacterPlayerControlListener(controllerControl, state));
			controllerControl.addStateListener(GameState.State.MENU, new SelectionPlayerControlListener(controllerControl, characterSelection));
		}

		// Add developer hotkeys
		addDeveloperHotkeys();

		characterViewPortTracker = new CharacterViewPortTracker(state.getPlayerCharacters());
	}

	private void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, ingameRenderer::toggleLighting);
		addDeveloperHotkey(Input.Keys.F2, ingameRenderer::toggleScene);
		addDeveloperHotkey(Input.Keys.F3, ingameRenderer::randomizeBaseLight);
	}

	private void addDeveloperHotkey(int keycode, Runnable runnable) {
		KeyboardToggle keyboardToggle = new KeyboardToggle(keycode);
		inputMultiplexer.addProcessor(keyboardToggle);
		Trigger trigger = new Trigger(keyboardToggle);
		trigger.addListener(runnable);
	}

	@Override
	public void render() {
		state.updateStateTime(Gdx.graphics.getDeltaTime());
		characterViewPortTracker.refresh(viewPort);

		// Render corresponding state
		if (state.getCurrentState() == GameState.State.MENU) {
			characterSelection.render();
		} else if (state.getCurrentState() == GameState.State.INGAME) {
			ingameRenderer.render();
		}

		// Game loop
		for (Iterator<Entity<?>> e = state.getEntities().iterator(); e.hasNext();) {
			Entity<?> entity = e.next();
			entity.think(state);
			entity.move(state);
			if (entity.isExpired(state.getStateTime())) {
				e.remove();
				state.getPlayerCharacters().remove(entity);
			}
		}

		state.refresh();
		this.frame += 1;
	}

	@Override
	public void dispose () {
		ingameRenderer.dispose();
		characterSelection.dispose();
		state.getTilesetManager().dispose();
		Light.dispose();
	}
}
