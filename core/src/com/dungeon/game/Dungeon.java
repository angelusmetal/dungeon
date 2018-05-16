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
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.render.effect.RenderEffect;
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
import com.dungeon.game.object.powerups.HealthPowerupFactory;
import com.dungeon.game.object.tombstone.TombstoneFactory;
import com.dungeon.game.object.torch.TorchFactory;
import com.dungeon.game.render.ViewPortRenderer;
import com.dungeon.game.state.CharacterPlayerControlListener;
import com.dungeon.game.state.CharacterSelection;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;
import com.dungeon.game.state.SelectionPlayerControlListener;
import com.moandjiezana.toml.Toml;

import java.util.Iterator;

public class Dungeon extends ApplicationAdapter {
	private static final double DEFAULT_SCALE = 3;
	private final Toml configuration;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private CharacterViewPortTracker characterViewPortTracker;
	private ViewPortRenderer viewPortRenderer;
	private CharacterSelection characterSelection;
	private EntityFactory entityFactory;

	private boolean fading = false;

	private long frame = 0;

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

		Toml controllerConfigs = configuration.getTable("controllers");

		for (Controller controller : Controllers.getControllers()) {
			System.out.println("Found controller: " + controller.getName());
			// Look for explicit controller config, or use defaults
			Toml controllerCfg = controllerConfigs.getTable('"' + controller.getName() + '"');
			int povCode = 0;
			int xAxis = 3;
			int yAxis = -2;
			if (controllerCfg != null) {
				povCode = controllerCfg.getLong("povCode", 0L).intValue();
				xAxis = controllerCfg.getLong("xAxis", 3L).intValue();
				yAxis = controllerCfg.getLong("yAxis", -2L).intValue();
			}
			DpadAnalogControl povControl = new DpadAnalogControl(povCode);
			StickAnalogControl analogControl = new StickAnalogControl(xAxis, yAxis);
			controller.addListener(povControl);
			controller.addListener(analogControl);
		}

		entityFactory = new EntityFactory();
		GameState.initialize(entityFactory, configuration);

		entityFactory.registerFactory(EntityType.EXIT, new ExitPlatformFactory());

		TombstoneFactory tombstoneFactory = new TombstoneFactory();
		entityFactory.registerFactory(EntityType.TORCH, new TorchFactory());
		entityFactory.registerFactory(EntityType.TOMBSTONE, tombstoneFactory);

		entityFactory.registerFactory(EntityType.GHOST, new GhostFactory());
		entityFactory.registerFactory(EntityType.SLIME, new SlimeFactory());
		entityFactory.registerFactory(EntityType.SLIME_ACID, new AcidSlimeFactory());
		entityFactory.registerFactory(EntityType.SLIME_FIRE, new FireSlimeFactory());

		entityFactory.registerFactory(EntityType.HEALTH_POWERUP, new HealthPowerupFactory());

		entityFactory.registerFactory(EntityType.ASSASIN, new AssassinFactory(tombstoneFactory));
		entityFactory.registerFactory(EntityType.THIEF, new ThiefFactory(tombstoneFactory));
		entityFactory.registerFactory(EntityType.WITCH, new WitchFactory(tombstoneFactory));

		viewPortRenderer = new ViewPortRenderer(viewPort);
		viewPortRenderer.initialize();
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

		characterViewPortTracker = new CharacterViewPortTracker(GameState.getPlayerCharacters());

		// Add watches
		GameState.console().watch("FPS", () -> Integer.toString(Gdx.graphics.getFramesPerSecond()));
//		GameState.console().watch("Time", () -> Float.toString(GameState.time()));
		GameState.console().watch("Level", () -> Integer.toString(GameState.getLevelCount()));
		GameState.console().watch("Render calls", () -> Integer.toString(viewPortRenderer.getRenderCalls()));
		GameState.console().watch("Frame time", () -> Float.toString(viewPortRenderer.getFrameTime()) + " ms");
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
		addDeveloperHotkey(Input.Keys.F4, viewPortRenderer::toggleHealthbars);
		addDeveloperHotkey(Input.Keys.F5, viewPortRenderer::toggleBoundingBox);
		addDeveloperHotkey(Input.Keys.F6, viewPortRenderer::toggleNoise);
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
		characterViewPortTracker.refresh(viewPort);

		// Game loop
		for (Iterator<Entity> e = GameState.getEntities().iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				e.remove();
				if (entity instanceof PlayerCharacter) {
					GameState.getPlayerCharacters().remove(entity);
				}
			}
		}
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
			viewPortRenderer.render();
		}

		// Render effects on top
		GameState.getRenderEffects().forEach(RenderEffect::render);

		GameState.refresh();

		if (!fading && GameState.getCurrentState() == GameState.State.INGAME && GameState.playersAlive() == 0) {
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
		viewPortRenderer.dispose();
		characterSelection.dispose();
		GameState.getTilesetManager().dispose();
		ResourceManager.instance().unloadAll();
	}
}
