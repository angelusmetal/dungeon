package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.input.GestureDetector;
import com.dungeon.engine.controller.analog.DpadAnalogControl;
import com.dungeon.engine.controller.analog.StickAnalogControl;
import com.dungeon.engine.controller.player.ControllerPlayerControlBundle;
import com.dungeon.engine.controller.player.KeyboardPlayerControlBundle;
import com.dungeon.engine.controller.player.PlayerControlBundle;
import com.dungeon.engine.controller.toggle.KeyboardToggle;
import com.dungeon.engine.controller.trigger.Trigger;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.viewport.ViewPortInputProcessor;
import com.dungeon.game.character.acidslime.AcidSlimeFactory;
import com.dungeon.game.character.assassin.AssassinFactory;
import com.dungeon.game.character.fireslime.FireSlimeFactory;
import com.dungeon.game.character.ghost.GhostFactory;
import com.dungeon.game.character.slime.SlimeFactory;
import com.dungeon.game.character.thief.ThiefFactory;
import com.dungeon.game.character.witch.WitchFactory;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.object.exit.ExitPlatformFactory;
import com.dungeon.game.object.powerups.HealthPowerup;
import com.dungeon.game.object.tombstone.Tombstone;
import com.dungeon.game.object.torch.Torch;
import com.dungeon.game.state.CharacterPlayerControlListener;
import com.dungeon.game.state.CharacterSelection;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.SelectionPlayerControlListener;
import com.moandjiezana.toml.Toml;

import java.util.Iterator;

public class Dungeon extends ApplicationAdapter {
	public static final double DEFAULT_SCALE = 3;
	private final Toml configuration;
	private GameState state;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private CharacterViewPortTracker characterViewPortTracker;
	private ViewPortRenderer viewPortRenderer;
	private CharacterSelection characterSelection;
	private EntityFactory entityFactory;

	private boolean fading = false;

	long frame = 0;

	public Dungeon(Toml configuration) {
		this.configuration = configuration;
	}

	@Override
	public void create () {
		initViewPort();
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
		state = new GameState(entityFactory);

		entityFactory.registerFactory(EntityType.EXIT, new ExitPlatformFactory(state));

		Tombstone.Factory tombstoneFactory = new Tombstone.Factory(state);
		entityFactory.registerFactory(EntityType.TORCH, new Torch.Factory(state));
		entityFactory.registerFactory(EntityType.TOMBSTONE, tombstoneFactory);

		entityFactory.registerFactory(EntityType.GHOST, new GhostFactory(state));
		entityFactory.registerFactory(EntityType.SLIME, new SlimeFactory(state));
		entityFactory.registerFactory(EntityType.SLIME_ACID, new AcidSlimeFactory(state));
		entityFactory.registerFactory(EntityType.SLIME_FIRE, new FireSlimeFactory(state));

		entityFactory.registerFactory(EntityType.HEALTH_POWERUP, new HealthPowerup.Factory(state));

		entityFactory.registerFactory(EntityType.ASSASIN, new AssassinFactory(state, tombstoneFactory));
		entityFactory.registerFactory(EntityType.THIEF, new ThiefFactory(state, tombstoneFactory));
		entityFactory.registerFactory(EntityType.WITCH, new WitchFactory(state, tombstoneFactory));

		viewPortRenderer = new ViewPortRenderer(state, viewPort);
		viewPortRenderer.initialize();
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

	private void initViewPort() {
		float scale = configuration.getDouble("viewport.scale", DEFAULT_SCALE).floatValue();
		int posX = configuration.getLong("viewport.posx", 0L).intValue();
		int posY = configuration.getLong("viewport.posy", 0L).intValue();
		int width = configuration.getLong("viewport.width", (long) Gdx.graphics.getWidth()).intValue();
		int height = configuration.getLong("viewport.height", (long) Gdx.graphics.getHeight()).intValue();
		viewPort = new ViewPort(posX, posY, width, height, scale);
		viewPortInputProcessor = new ViewPortInputProcessor(viewPort);
	}

	private void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, viewPortRenderer::toggleLighting);
		addDeveloperHotkey(Input.Keys.F2, viewPortRenderer::toggleScene);
		addDeveloperHotkey(Input.Keys.F3, viewPortRenderer::randomizeBaseLight);
	}

	private void addDeveloperHotkey(int keycode, Runnable runnable) {
		KeyboardToggle keyboardToggle = new KeyboardToggle(keycode);
		inputMultiplexer.addProcessor(keyboardToggle);
		Trigger trigger = new Trigger(keyboardToggle);
		trigger.addListener(runnable);
	}

	float nextFpsDisplay = 0;
	int frameCnt = 0;

	@Override
	public void render() {
		state.updateStateTime(Gdx.graphics.getDeltaTime());
		characterViewPortTracker.refresh(viewPort);

		// Game loop
		for (Iterator<Entity> e = state.getEntities().iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.think(state);
			entity.move(state);
			if (entity.isExpired(state.getStateTime())) {
				e.remove();
				state.getPlayerCharacters().remove(entity);
			}
		}

		// Render corresponding state
		if (state.getCurrentState() == GameState.State.MENU) {
			characterSelection.render();
		} else if (state.getCurrentState() == GameState.State.INGAME) {
			viewPortRenderer.render();
		}

		// Render effects on top
		state.getRenderEffects().forEach(e -> e.render(state));

		state.refresh();

		if (!fading && state.getCurrentState() == GameState.State.INGAME && state.playersAlive() == 0) {
			fading = true;
			state.addRenderEffect(FadeEffect.fadeOutDeath(state.getStateTime(), () -> {
				state.setCurrentState(GameState.State.MENU);
				state.addRenderEffect(FadeEffect.fadeIn(state.getStateTime()));
				fading = false;
			}));
		}

		this.frame += 1;
		
		frameCnt++;
		if (state.getStateTime() >= nextFpsDisplay) {
			System.out.println("FPS: " + frameCnt);
			frameCnt = 0;
			nextFpsDisplay = state.getStateTime() + 1;
		}
	}

	@Override
	public void dispose () {
		viewPortRenderer.dispose();
		characterSelection.dispose();
		state.getTilesetManager().dispose();
		ResourceManager.instance().unloadAll();
	}
}
