package com.dungeon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.character.CharacterControl;
import com.dungeon.engine.controller.character.ControllerCharacterControl;
import com.dungeon.engine.controller.character.KeyboardCharacterControl;
import com.dungeon.engine.controller.directional.AnalogDirectionalControl;
import com.dungeon.engine.controller.directional.DirectionalListener;
import com.dungeon.engine.controller.directional.PovDirectionalControl;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.viewport.CharacterViewPortTracker;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.engine.viewport.ViewPortInputProcessor;
import com.dungeon.game.character.Assasin;
import com.dungeon.game.character.Ghost;
import com.dungeon.game.character.Thief;
import com.dungeon.game.character.Witch;
import com.dungeon.game.level.Room;

import java.util.Comparator;
import java.util.Iterator;

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

		// Add keyboard controller
		CharacterControl keyboardControl = new KeyboardCharacterControl(state, inputMultiplexer, this::getStartingPosition, this::getNewPlayer);

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			CharacterControl controllerControl = new ControllerCharacterControl(state, controller, this::getStartingPosition, this::getNewPlayer);
		}

		characterViewPortTracker = new CharacterViewPortTracker(state.getPlayerCharacters());

		// Create ghosts in each room, to begin with
		for (int r = 1; r < state.getLevel().rooms.size(); ++r) {
			Room room = state.getLevel().rooms.get(r);
			// Ghost 1
			Vector2 position = new Vector2((room.left + 2) * state.getLevelTileset().tile_size, (room.bottom + 2) * state.getLevelTileset().tile_size);
			Ghost ghost = new Ghost(state, position);
			state.addEntity(ghost);
			// Ghost 2
			position = new Vector2((room.left + 3) * state.getLevelTileset().tile_size, (room.bottom + 2) * state.getLevelTileset().tile_size);
			ghost = new Ghost(state, position);
			state.addEntity(ghost);
			// Ghost 3
			position = new Vector2((room.left + 2) * state.getLevelTileset().tile_size, (room.bottom + 3) * state.getLevelTileset().tile_size);
			ghost = new Ghost(state, position);
			state.addEntity(ghost);
		}
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
			return new Vector2(state.getLevel().rooms.get(0).startX, state.getLevel().rooms.get(0).startY);
		} else {
			Vector2 refPos = state.getPlayerCharacters().get(0).getPos();
			return new Vector2(refPos.x / state.getLevelTileset().tile_size + 1, refPos.y / state.getLevelTileset().tile_size);
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
		int tWidth = state.getLevelTileset().tile_size;
		int tHeight = state.getLevelTileset().tile_size;
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
