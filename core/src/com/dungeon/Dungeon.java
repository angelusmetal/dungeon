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
import com.dungeon.character.King;
import com.dungeon.character.Projectile;
import com.dungeon.movement.MovableControllerAdapter;
import com.dungeon.movement.MovableInputProcessor;
import com.dungeon.viewport.CharacterViewPortTracker;
import com.dungeon.viewport.ViewPort;
import com.dungeon.viewport.ViewPortInputProcessor;

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
	public float stateTime = 0f;

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
		{
			Character character = new King(state.getTilesetManager().getCharactersTileset(), this);
			character.moveTo(new Vector2(startX * state.getLevelTileset().tile_width, startY * state.getLevelTileset().tile_height));
			state.addCharacter(character);
			movableInputProcessor.addPovController(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, character);
			movableInputProcessor.addButtonController(Input.Keys.SPACE, code -> character.fire(state));
		}

		// Add an extra controller for each physical one
		for (Controller controller : Controllers.getControllers()) {
			System.out.println(controller.getName());
			Character character = new King(state.getTilesetManager().getCharactersTileset(), this);
			character.moveTo(new Vector2(startX * state.getLevelTileset().tile_width, startY * state.getLevelTileset().tile_height));
			state.addCharacter(character);
			// Add all 3 input methods to the character
			MovableControllerAdapter movableControllerAdapter = new MovableControllerAdapter();
			//movableControllerAdapter.addAxisController(1, 0, character);
			movableControllerAdapter.addAxisController(3, 2, character);
			movableControllerAdapter.addPovController(0, character);
			movableControllerAdapter.addButtonController(0, code -> character.fire(state));
			controller.addListener(movableControllerAdapter);
			//controller.addListener(new PrintingControllerListener());
		}

		characterViewPortTracker = new CharacterViewPortTracker(state.getCharacters());

	}

	@Override
	public void render () {
		//Gdx.gl.glClearColor(145f/255f, 176f/255f, 154f/255f, 1);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateTime += Gdx.graphics.getDeltaTime();
		characterViewPortTracker.refresh(viewPort);
		batch.begin();
		drawMap();
		for (Character character : state.getCharacters()) {
			character.draw(batch, viewPort, stateTime);
			character.move(state);
		}
		for (Iterator<Projectile> p = state.getProjectiles().iterator(); p.hasNext();) {
			Projectile projectile = p.next();
			projectile.draw(batch, viewPort, stateTime);
			projectile.move(state);
			if (projectile.isDone(stateTime)) {
				p.remove();
			}
		}
		batch.end();
		this.frame += 1;
	}

	private void drawMap() {
		for (int x = 0; x < state.getLevel().map.length; x++) {
			for (int y = 0; y < state.getLevel().map[0].length; y++) {
				TextureRegion textureRegion = state.getLevel().map[x][y].animation.getKeyFrame(stateTime, true);
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
