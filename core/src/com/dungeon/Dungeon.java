package com.dungeon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.character.Character;
import com.dungeon.character.Entity;
import com.dungeon.character.King;
import com.dungeon.character.Projectile;
import com.dungeon.movement.MovableControllerAdapter;
import com.dungeon.movement.MovableInputProcessor;
import com.dungeon.viewport.CharacterViewPortTracker;
import com.dungeon.viewport.ViewPort;
import com.dungeon.viewport.ViewPortInputProcessor;

import java.util.Iterator;
import java.util.stream.Collectors;

public class Dungeon extends ApplicationAdapter {
	public static final float INITIAL_SCALE = 4;
	private SpriteBatch batch;
	private GameState state;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private MovableInputProcessor movableInputProcessor = new MovableInputProcessor();
	private CharacterViewPortTracker characterViewPortTracker;

	long frame = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		viewPort = new ViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, INITIAL_SCALE);
		viewPortInputProcessor = new ViewPortInputProcessor(viewPort);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(viewPortInputProcessor);
		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		inputMultiplexer.addProcessor(movableInputProcessor);
		Gdx.input.setInputProcessor(inputMultiplexer);

		state = new GameState(viewPort);
		state.generateNewLevel();

		int startX = state.getLevel().rooms.get(0).topLeft.x + 1;
		int startY = state.getLevel().rooms.get(0).topLeft.y - 1;

		// Add keyboard controller
		CharacterControllerMapper keyboardMapper = new CharacterControllerMapper() {
			@Override
			void bind() {
				if (character == null || character.isExpired(state.getStateTime())) {
					Vector2 startingPosition = getStartingPosition();
					character = new King(state);
					character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
					state.addCharacter(character);
					movableInputProcessor.addPovController(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, character);
					movableInputProcessor.addButtonController(Input.Keys.SPACE, code -> character.fire(state));
				}
			}

			@Override
			void unbind() {
				if (character != null) {
					character.setExpired(true);
					character = null;
					movableInputProcessor.clear();
				}
			}
		};
		movableInputProcessor.addButtonController(Input.Keys.ENTER, code -> keyboardMapper.bind());

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			MovableControllerAdapter movableControllerAdapter = new MovableControllerAdapter();
			CharacterControllerMapper controllerMapper = new CharacterControllerMapper() {
				@Override
				void bind() {
					if (character == null|| character.isExpired(state.getStateTime())) {
						Vector2 startingPosition = getStartingPosition();
						character = new King(state);
						character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
						state.addCharacter(character);
						// Add all 3 input methods to the character
						movableControllerAdapter.addAxisController(3, 2, character);
						movableControllerAdapter.addPovController(0, character);
						movableControllerAdapter.addButtonController(0, code -> character.fire(state));
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
			controller.addListener(movableControllerAdapter);
			controller.addListener(new PrintingControllerListener());
			movableControllerAdapter.addButtonController(9, code -> controllerMapper.bind());
		}

		characterViewPortTracker = new CharacterViewPortTracker(state.getCharacters());

	}

	private Vector2 getStartingPosition() {
		if (state.getCharacters().isEmpty()) {
			int startX = state.getLevel().rooms.get(0).topLeft.x + 1;
			int startY = state.getLevel().rooms.get(0).topLeft.y - 1;
			System.out.println("first spawn: " + new Vector2(startX, startY));
			return new Vector2(startX, startY);
		} else {
			Vector2 refPos = state.getCharacters().get(0).getPos();
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
		for (Iterator<Entity<?>> e = state.getEntities().iterator(); e.hasNext();) {
			Entity<?> entity = e.next();
			entity.draw(batch, viewPort, state.getStateTime());
			entity.move(state);
			if (entity.isExpired(state.getStateTime())) {
				e.remove();
				state.getCharacters().remove(entity);
			}
		}
		batch.end();
		this.frame += 1;
	}

	private void drawMap() {
		for (int x = 0; x < state.getLevel().map.length; x++) {
			for (int y = 0; y < state.getLevel().map[0].length; y++) {
				TextureRegion textureRegion = state.getLevel().map[x][y].animation.getKeyFrame(state.getStateTime(), true);
				batch.draw(textureRegion, (x * state.getLevelTileset().tile_width - viewPort.xOffset) * viewPort.scale, (y * state.getLevelTileset().tile_height - viewPort.yOffset) * viewPort.scale, textureRegion.getRegionWidth() * viewPort.scale, textureRegion.getRegionHeight() * viewPort.scale);
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		state.getTilesetManager().dispose();
	}
}
