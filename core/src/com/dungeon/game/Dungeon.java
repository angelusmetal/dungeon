package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.character.CharacterControl;
import com.dungeon.engine.controller.character.ControllerCharacterControl;
import com.dungeon.engine.controller.character.KeyboardCharacterControl;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.directional.DirectionalListener;
import com.dungeon.engine.controller.directional.PovDirectionalControl;
import com.dungeon.engine.controller.trigger.KeyboardTriggerControl;
import com.dungeon.engine.controller.trigger.TriggerControl;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.viewport.ViewPortInputProcessor;
import com.dungeon.game.character.Assasin;
import com.dungeon.game.character.Ghost;
import com.dungeon.game.character.Thief;
import com.dungeon.game.character.Witch;
import com.dungeon.game.level.Room;
import com.dungeon.game.object.HealthPowerup;
import com.dungeon.game.object.Torch;

import java.util.Comparator;
import java.util.Iterator;

public class Dungeon extends ApplicationAdapter {
	public static final float INITIAL_SCALE = 4;
	private GameState state;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private CharacterViewPortTracker characterViewPortTracker;
	private IngameRenderer ingameRenderer;

	long frame = 0;

	@Override
	public void create () {
		viewPort = new ViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, INITIAL_SCALE);
		viewPortInputProcessor = new ViewPortInputProcessor(viewPort);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(viewPortInputProcessor);
		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		//inputMultiplexer.addProcessor(movableInputProcessor);
		Gdx.input.setInputProcessor(inputMultiplexer);

		// Testing stuff; remove
		DirectionalListener dListener = (pov, vector) -> {
			System.out.println("POV: " + pov + "; Vector: " + vector);
		};
		for (Controller controller : Controllers.getControllers()) {
			PovDirectionalControl povControl = new PovDirectionalControl(0);
			AnalogDirectionalControl analogControl = new AnalogDirectionalControl(3, -2);
			controller.addListener(povControl);
			controller.addListener(analogControl);
			povControl.addListener(dListener);
			analogControl.addListener(dListener);
		}

		state = new GameState(viewPort);
		Light.initialize();
		ingameRenderer = new IngameRenderer(state, viewPort);
		ingameRenderer.initialize();

		state.generateNewLevel();

		// Add keyboard controller
		CharacterControl keyboardControl = new KeyboardCharacterControl(state, inputMultiplexer, this::getStartingPosition, this::getNewPlayer);

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			CharacterControl controllerControl = new ControllerCharacterControl(state, controller, this::getStartingPosition, this::getNewPlayer);
		}

		// Add developer hotkeys
		addDeveloperHotkeys();

		characterViewPortTracker = new CharacterViewPortTracker(state.getPlayerCharacters());

		// Create ghosts in each room, to begin with
		for (int r = 1; r < state.getLevel().rooms.size(); ++r) {
			Room room = state.getLevel().rooms.get(r);
			// Create ghosts in each room
			final int skip = (int) (Math.random() * room.spawnPoints.size());
			room.spawnPoints.stream().skip(skip).forEach(v -> {
				Ghost ghost = new Ghost(state, v.cpy().scl(state.getLevelTileset().tile_size));
				state.addEntity(ghost);
			});
			// A 40% chance of spawning one powerup
			if (Math.random() < 0.4d) {
				Vector2 spawnPosition = room.spawnPoints.get((int) (Math.random() * room.spawnPoints.size()));
				HealthPowerup powerup = new HealthPowerup(state, spawnPosition.cpy().scl(state.getLevelTileset().tile_size));
				state.addEntity(powerup);
			}
		}
		for (Room room : state.getLevel().rooms) {
			room.torches.forEach(v -> {
				Torch torch = new Torch(state, v.cpy().scl(state.getLevelTileset().tile_size));
				state.addEntity(torch);
			});
		}

	}

	private void addDeveloperHotkeys() {
		addDeveloperHotkey(Input.Keys.F1, ingameRenderer::toggleLighting);
		addDeveloperHotkey(Input.Keys.F2, ingameRenderer::toggleScene);
		addDeveloperHotkey(Input.Keys.F3, ingameRenderer::randomizeBaseLight);
	}

	private void addDeveloperHotkey(int keycode, Runnable runnable) {
		System.out.println("Adding developer trigger for " + keycode);
		KeyboardTriggerControl trigger = new KeyboardTriggerControl(keycode);
		trigger.addListener(b -> {
			System.out.println("WOW");
			if (b) {
				runnable.run();
			}
		});
		inputMultiplexer.addProcessor(trigger);
	}

	private PlayerCharacter getNewPlayer(Vector2 origin) {
		boolean hasAssasin = false, hasThief = false, hasWitch = false;
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
			if (playerCharacter instanceof Assasin) {
				hasAssasin = true;
			} else if (playerCharacter instanceof Thief) {
				hasThief = true;
			} else if (playerCharacter instanceof Witch) {
				hasWitch = true;
			}
		}
		if (!hasWitch) {
			return new Witch(state, origin);
		} else if (!hasThief) {
			return new Thief(state, origin);
		} else {
			return new Assasin(state, origin);
		}

	}

	private Vector2 getStartingPosition() {
		if (state.getPlayerCharacters().isEmpty()) {
			return state.getLevel().rooms.get(0).spawnPoints.get(0).cpy();
		} else {
			Vector2 refPos = state.getPlayerCharacters().get(0).getPos();
			return new Vector2(refPos.x / state.getLevelTileset().tile_size + 1, refPos.y / state.getLevelTileset().tile_size);
		}
	}

	@Override
	public void render() {
		state.updateStateTime(Gdx.graphics.getDeltaTime());
		characterViewPortTracker.refresh(viewPort);

		// Render in-game
		ingameRenderer.render();

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
		state.getTilesetManager().dispose();
		Light.dispose();
	}
}
