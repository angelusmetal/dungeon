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
import com.badlogic.gdx.math.Vector2;
import com.dungeon.character.Character;
import com.dungeon.character.GhostTileset;
import com.dungeon.character.Projectile;
import com.dungeon.character.ProjectileTileset;
import com.dungeon.level.Level;
import com.dungeon.level.ProceduralLevelGenerator;
import com.dungeon.movement.MovableControllerAdapter;
import com.dungeon.tileset.DungeonTilesetDark;
import com.dungeon.tileset.Tile;
import com.dungeon.viewport.CharacterViewPortTracker;
import com.dungeon.viewport.ViewPort;
import com.dungeon.viewport.ViewPortInputProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Dungeon extends ApplicationAdapter {
	public static final float INITIAL_SCALE = 4;
	private SpriteBatch batch;
	private DungeonTilesetDark tileset;
	private GhostTileset ghostTileset;
	private ProjectileTileset projectileTileset;
	private ViewPort viewPort;
	private InputMultiplexer inputMultiplexer;
	private ViewPortInputProcessor viewPortInputProcessor;
	private CharacterViewPortTracker characterViewPortTracker;
	private List<Character> characters = new ArrayList<>();
	private List<Projectile> projectiles = new ArrayList<>();

	long frame = 0;
	float stateTime = 0f;

	public final int MAP_WIDTH = 100;
	public final int MAP_HEIGHT = 100;
	private Level level;

	@Override
	public void create () {
		batch = new SpriteBatch();
		tileset = new DungeonTilesetDark();
		ghostTileset = new GhostTileset();
		projectileTileset = new ProjectileTileset();
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(MAP_WIDTH, MAP_HEIGHT);
		level = generator.generateLevel(tileset);
		viewPort = new ViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, INITIAL_SCALE);
		viewPortInputProcessor = new ViewPortInputProcessor(viewPort);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(viewPortInputProcessor);
		inputMultiplexer.addProcessor(new GestureDetector(viewPortInputProcessor));
		Gdx.input.setInputProcessor(inputMultiplexer);

		int startX = level.rooms.get(0).topLeft.x + 1;
		int startY = level.rooms.get(0).topLeft.y - 1;

		for (Controller controller : Controllers.getControllers()) {
			System.out.println(controller.getName());
			Character character = new Character(ghostTileset.HOVER_ANIMATION);
			character.setPos(new Vector2(startX * tileset.tile_width, startY * tileset.tile_height));
			characters.add(character);
			// Add all 3 input methods to the character
			MovableControllerAdapter movableControllerAdapter = new MovableControllerAdapter();
			//movableControllerAdapter.addAxisController(1, 0, character);
			movableControllerAdapter.addAxisController(3, 2, character);
			movableControllerAdapter.addPovController(0, character);
			movableControllerAdapter.addButtonController(0, code -> {
				Projectile projectile = new Projectile(projectileTileset.PROJECTILE_ANIMATION, 10, stateTime);
				projectile.setPos(character.getPos());
				projectile.getPos().add(character.getFrame(stateTime).getRegionWidth() / 2, character.getFrame(stateTime).getRegionHeight() / 2);
				projectile.setSelfMovement(character.getSelfMovement());
				float len = projectile.getSelfMovement().len();
				projectile.getSelfMovement().scl(5 / len);
				projectiles.add(projectile);
				System.out.println("FIRE!");
			});
			controller.addListener(movableControllerAdapter);
			//controller.addListener(new PrintingControllerListener());
		}

		characterViewPortTracker = new CharacterViewPortTracker(characters);

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
		for (Character character : characters) {
			TextureRegion characterFrame = character.getFrame(stateTime);
			batch.draw(characterFrame, (character.getPos().x - viewPort.xOffset) * viewPort.scale, (character.getPos().y - viewPort.yOffset) * viewPort.scale, characterFrame.getRegionWidth() * viewPort.scale, characterFrame.getRegionHeight() * viewPort.scale);
			character.move();
		}
		for (Iterator<Projectile> p = projectiles.iterator(); p.hasNext();) {
			Projectile projectile = p.next();
			TextureRegion projectileFrame = projectile.getFrame(stateTime);
			batch.draw(projectileFrame, (projectile.getPos().x - viewPort.xOffset) * viewPort.scale, (projectile.getPos().y - viewPort.yOffset) * viewPort.scale, projectileFrame.getRegionWidth() * viewPort.scale, projectileFrame.getRegionHeight() * viewPort.scale);
			projectile.move();
			if (projectile.isDone(stateTime)) {
				p.remove();
			}
		}
		batch.end();
		this.frame += 1;
	}

	private void drawMap() {
		for (int x = 0; x < MAP_WIDTH; x++) {
			for (int y = 0; y < MAP_WIDTH; y++) {
				TextureRegion textureRegion = level.map[x][y].animation.getKeyFrame(stateTime, true);
				batch.draw(textureRegion, (x * tileset.tile_width - viewPort.xOffset) * viewPort.scale, (y * tileset.tile_height - viewPort.yOffset) * viewPort.scale, textureRegion.getRegionWidth() * viewPort.scale, textureRegion.getRegionHeight() * viewPort.scale);
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		tileset.dispose();
	}
}
