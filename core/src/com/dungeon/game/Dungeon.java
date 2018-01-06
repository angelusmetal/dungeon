package com.dungeon.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.directional.DirectionalListener;
import com.dungeon.engine.controller.directional.KeyboardDirectionalControl;
import com.dungeon.engine.controller.directional.PovDirectionalControl;
import com.dungeon.engine.controller.trigger.ControllerTriggerControl;
import com.dungeon.engine.controller.trigger.KeyboardTriggerControl;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.character.*;
import com.dungeon.game.level.Room;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.viewport.ViewPortInputProcessor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

public class Dungeon extends ApplicationAdapter {
	public static final float INITIAL_SCALE = 4;
	private SpriteBatch batch;
	private GameState state;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private CharacterViewPortTracker characterViewPortTracker;

	long frame = 0;
	private Comparator<? super Entity<?>> comp = (e1, e2) ->
		e1.getPos().y > e2.getPos().y ? -1 :
		e1.getPos().y < e2.getPos().y ? 1 :
		e1.getPos().x < e2.getPos().x ? -1 :
		e1.getPos().x > e2.getPos().x ? 1 : 0;

	// TODO Maybe move the multiplexer and these methods to an input-dedicated class
	public void addInputProcessor(InputProcessor processor) {
		inputMultiplexer.addProcessor(processor);
	}

	public void removeInputProcessor(InputProcessor processor) {
		inputMultiplexer.removeProcessor(processor);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
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
		state.generateNewLevel();

		// These should be moved into their own class with multiple implementations (keyboard, controller, etc)
		// Add keyboard controller
		CharacterControllerMapper keyboardMapper = new CharacterControllerMapper() {
			@Override
			void bind() {
				if (character == null || character.isExpired(state.getStateTime())) {
					Vector2 startingPosition = getStartingPosition();
					character = getNewPlayer();
					character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
					state.addPlayerCharacter(character);
					KeyboardDirectionalControl keyboardControl = new KeyboardDirectionalControl(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT);
					addInputProcessor(keyboardControl);
					keyboardControl.addListener((pov, vec) -> character.setSelfMovement(vec));
					KeyboardTriggerControl keyboardTrigger = new KeyboardTriggerControl(Input.Keys.SPACE);
					addInputProcessor(keyboardTrigger);
					keyboardTrigger.addListener((bool) -> { if (bool) character.fire(state);});
				}
			}

			@Override
			void unbind() {
				if (character != null) {
					character.setExpired(state, true);
					character = null;
				}
			}
		};
		KeyboardTriggerControl keyboardStartTrigger = new KeyboardTriggerControl(Input.Keys.ENTER);
		keyboardStartTrigger.addListener((bool) -> keyboardMapper.bind());
		addInputProcessor(keyboardStartTrigger);

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			CharacterControllerMapper controllerMapper = new CharacterControllerMapper() {
				@Override
				void bind() {
					if (character == null|| character.isExpired(state.getStateTime())) {
						Vector2 startingPosition = getStartingPosition();
						character = getNewPlayer();
						character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
						state.addPlayerCharacter(character);
						// Add all 3 input methods to the character
						PovDirectionalControl povControl = new PovDirectionalControl(0);
						AnalogDirectionalControl analogControl = new AnalogDirectionalControl(3, -2);
						ControllerTriggerControl shootControl = new ControllerTriggerControl(0);
						// Registered should be handled by the controls themselves via double dispatch
						controller.addListener(povControl);
						controller.addListener(analogControl);
						controller.addListener(shootControl);
						DirectionalListener mover = (pov, vec) -> character.setSelfMovement(vec);
						Consumer<Boolean> shooter = (bool) -> { if (bool) character.fire(state);};
						povControl.addListener(mover);
						analogControl.addListener(mover);
						shootControl.addListener(shooter);
					}
				}

				@Override
				void unbind() {
					if (character != null) {
						character = null;
						//controller.removeListener(movableControllerAdapter);
					}
				}
			};
			ControllerTriggerControl startControl = new ControllerTriggerControl(9);
			startControl.addListener((bool) -> controllerMapper.bind());
			controller.addListener(startControl);
		}

		characterViewPortTracker = new CharacterViewPortTracker(state.getPlayerCharacters());

		// Create a ghost in each room, to begin with
		for (int r = 1; r < state.getLevel().rooms.size(); ++r) {
			Room room = state.getLevel().rooms.get(r);
			for (int i = 0; i < 3; ++i) {
				int startX = room.topLeft.x + 1 + i;
				int startY = room.topLeft.y - 1 - i;
				Vector2 position = new Vector2(startX * state.getLevelTileset().tile_width, startY * state.getLevelTileset().tile_height);
				Ghost ghost = new Ghost(state);
				ghost.moveTo(position);
				state.addEntity(ghost);
			}
		}
	}

	private PlayerCharacter getNewPlayer() {
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
			return new Witch(state);
		} else if (!hasThief) {
			return new Thief(state);
		} else {
			return new Assasin(state);
		}

	}

	private Vector2 getStartingPosition() {
		if (state.getPlayerCharacters().isEmpty()) {
			int startX = state.getLevel().rooms.get(0).topLeft.x + 1;
			int startY = state.getLevel().rooms.get(0).topLeft.y - 1;
			return new Vector2(startX, startY);
		} else {
			Vector2 refPos = state.getPlayerCharacters().get(0).getPos();
			return new Vector2(refPos.x / state.getLevelTileset().tile_width, refPos.y / state.getLevelTileset().tile_height);
		}
	}

	@Override
	public void render () {
		//Gdx.gl.glClearColor(145f/255f, 176f/255f, 154f/255f, 1);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		state.updateStateTime(Gdx.graphics.getDeltaTime());
		characterViewPortTracker.refresh(viewPort);
		batch.begin();
		drawMap();
		// Iterate entities in render order and draw them
		state.getEntities().stream().sorted(comp).forEach(e -> e.draw(state, batch, viewPort));

		for (Iterator<Entity<?>> e = state.getEntities().iterator(); e.hasNext();) {
			Entity<?> entity = e.next();
			entity.move(state);
			entity.think(state);
			if (entity.isExpired(state.getStateTime())) {
				e.remove();
				state.getPlayerCharacters().remove(entity);
			}
		}
		batch.end();
		state.refresh();
		this.frame += 1;
	}

	private void drawMap() {
		// Only render the visible portion of the map
		int tWidth = state.getLevelTileset().tile_width;
		int tHeight = state.getLevelTileset().tile_height;
		int minX = Math.max(0, viewPort.xOffset / tWidth);
		int maxX = Math.min(state.getLevel().map.length - 1, (viewPort.xOffset + viewPort.width) / tWidth) + 1;
		int minY = Math.max(0, viewPort.yOffset / tHeight - 1);
		int maxY = Math.min(state.getLevel().map[0].length - 1, (viewPort.yOffset + viewPort.height) / tHeight);
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				TextureRegion textureRegion = state.getLevel().map[x][y].animation.getKeyFrame(state.getStateTime(), true);
				batch.draw(textureRegion, (x * tWidth - viewPort.xOffset) * viewPort.scale, (y * tHeight - viewPort.yOffset) * viewPort.scale, textureRegion.getRegionWidth() * viewPort.scale, textureRegion.getRegionHeight() * viewPort.scale);
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		state.getTilesetManager().dispose();
	}
}
