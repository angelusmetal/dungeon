package com.dungeon.game;

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
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.character.*;
import com.dungeon.game.level.Room;
import com.dungeon.engine.movement.MovableControllerAdapter;
import com.dungeon.engine.movement.MovableInputProcessor;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.viewport.ViewPortInputProcessor;

import java.util.Comparator;
import java.util.Iterator;

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
	private Comparator<? super Entity<?>> comp = (e1, e2) ->
		e1.getPos().y > e2.getPos().y ? -1 :
		e1.getPos().y < e2.getPos().y ? 1 :
		e1.getPos().x < e2.getPos().x ? -1 :
		e1.getPos().x > e2.getPos().x ? 1 : 0;

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

		// Add keyboard controller
		CharacterControllerMapper keyboardMapper = new CharacterControllerMapper() {
			@Override
			void bind() {
				if (character == null || character.isExpired(state.getStateTime())) {
					Vector2 startingPosition = getStartingPosition();
					character = getNewPlayer();
					character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
					state.addPlayerCharacter(character);
					movableInputProcessor.addPovController(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, character);
					movableInputProcessor.addButtonController(Input.Keys.SPACE, code -> character.fire(state));
				}
			}

			@Override
			void unbind() {
				if (character != null) {
					character.setExpired(state, true);
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
						character = getNewPlayer();
						character.moveTo(new Vector2(startingPosition.x * state.getLevelTileset().tile_width, startingPosition.y * state.getLevelTileset().tile_height));
						state.addPlayerCharacter(character);
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
		boolean hasKing = false, hasThief = false, hasMonk = false;
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
			if (playerCharacter instanceof King) {
				hasKing = true;
			} else if (playerCharacter instanceof Thief) {
				hasThief = true;
			} else if (playerCharacter instanceof Monk) {
				hasMonk = true;
			}
		}
		if (!hasKing) {
			return new King(state);
		} else if (!hasThief) {
			return new Thief(state);
		} else {
			return new Monk(state);
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
		int maxX = Math.min(state.getLevel().map.length - 1, (viewPort.xOffset + viewPort.width) / tWidth);
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
