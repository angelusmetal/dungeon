package com.dungeon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.dungeon.movement.MovableControllerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Dungeon extends ApplicationAdapter {
	public static final float INITIAL_SCALE = 4;
	private SpriteBatch batch;
	private DungeonTileset tileset;
	private GhostTileset ghostTileset;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private List<Character> characters = new ArrayList<>();

	long frame = 0;
	float stateTime = 0f;

	public final int MAP_WIDTH = 100;
	public final int MAP_HEIGHT = 100;
	private Tile[][] map;

	@Override
	public void create () {
		batch = new SpriteBatch();
		tileset = new DungeonTileset();
		ghostTileset = new GhostTileset();
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(MAP_WIDTH, MAP_HEIGHT);
		map = generator.generateLevel(tileset);
		viewPort = new ViewPort(0, 0, INITIAL_SCALE);
		viewPortInputProcessor = new ViewPortInputProcessor(viewPort);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(viewPortInputProcessor);
		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		Gdx.input.setInputProcessor(inputMultiplexer);


		for (Controller controller : Controllers.getControllers()) {
			System.out.println(controller.getName());
			Character character = new Character(ghostTileset.HOVER_ANIMATION);
			characters.add(character);
			// Add all 3 input methods to the character
			MovableControllerAdapter movableControllerAdapter = new MovableControllerAdapter();
			movableControllerAdapter.addAxisController(1, 0, character);
			movableControllerAdapter.addAxisController(3, 2, character);
			movableControllerAdapter.addPovController(0, character);
			controller.addListener(movableControllerAdapter);
			controller.addListener(new PrintingControllerListener());
		}

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(145f/255f, 176f/255f, 154f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		drawMap();
		for (Character character : characters) {
			TextureRegion characterFrame = character.getFrame(stateTime);
			batch.draw(characterFrame, (character.getPos().x + viewPort.xOffset) * viewPort.scale, (character.getPos().y + viewPort.yOffset) * viewPort.scale, characterFrame.getRegionWidth() * viewPort.scale, characterFrame.getRegionHeight() * viewPort.scale);
			character.move();
		}
		batch.end();
		this.frame += 1;
	}

	private void drawMap() {
		for (int x = 0; x < MAP_WIDTH; x++) {
			for (int y = 0; y < MAP_WIDTH; y++) {
				TextureRegion textureRegion = map[x][y].animation.getKeyFrame(stateTime, true);
				batch.draw(textureRegion, (x * tileset.tile_width + viewPort.xOffset) * viewPort.scale, (y * tileset.tile_height + viewPort.yOffset) * viewPort.scale, textureRegion.getRegionWidth() * viewPort.scale, textureRegion.getRegionHeight() * viewPort.scale);
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		tileset.dispose();
	}
}
