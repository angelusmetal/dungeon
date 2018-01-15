package com.dungeon.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.SolidTile;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.tileset.DungeonVioletTileset;
import com.dungeon.game.tileset.TilesetManager;
import com.dungeon.engine.viewport.ViewPort;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameState {
	public final int MAP_WIDTH = 100;
	public final int MAP_HEIGHT = 100;

	private float stateTime;
	private Level level;
	private TilesetManager tilesetManager;
	private ViewPort viewPort;
	private World world;

	private List<PlayerCharacter> playerCharacters = new LinkedList<>();
	private List<Entity<?>> entities = new LinkedList<>();

	private List<PlayerCharacter> newPlayerCharacters = new LinkedList<>();
	private List<Entity<?>> newEntities = new LinkedList<>();

	public GameState(ViewPort viewPort) {
		this.stateTime = 0;
		this.tilesetManager = new TilesetManager();
		this.viewPort = viewPort;
		this.world = new World(Vector2.Zero, true);
		this.world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				Object a = contact.getFixtureA().getBody().getUserData();
				Object b = contact.getFixtureB().getBody().getUserData();
				if (a instanceof Entity && b instanceof Entity) {
					((Entity) a).beginContact(GameState.this, ((Entity) b));
					((Entity) b).beginContact(GameState.this, ((Entity) a));
				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});
	}

	public float getStateTime() {
		return stateTime;
	}

	public void updateStateTime(float deltaTime) {
		stateTime += deltaTime;
	}

	public Level getLevel() {
		return level;
	}

	public TilesetManager getTilesetManager() {
		return tilesetManager;
	}

	public ViewPort getViewPort() {
		return viewPort;
	}

	public DungeonVioletTileset getLevelTileset() {
		// TODO Should DungeonVioletTileset implement a generic level Tileset class/interface?
		return tilesetManager.getDungeonVioletTileset();
	}

	public void generateNewLevel() {
		ProceduralLevelGenerator generator = new ProceduralLevelGenerator(MAP_WIDTH, MAP_HEIGHT);
		level = generator.generateLevel(getLevelTileset());

		// TODO Create objects for walls and move this code accordingly
		for (int col = 0; col < MAP_WIDTH; ++col) {
			for (int row = 0; row < MAP_HEIGHT; ++row) {
				if (!level.walkableTiles[col][row]) {
					SolidTile tile = new SolidTile(new Vector2(col, row));
					tile.attachToPhysics(this);
				}
			}
		}
	}
	public void addEntity(Entity<?> entity) {
		newEntities.add(entity);
	}

	public void addPlayerCharacter(PlayerCharacter character) {
		newPlayerCharacters.add(character);
		newEntities.add(character);
	}

	public List<Entity<?>> getEntities() {
		return entities;
	}

	public List<PlayerCharacter> getPlayerCharacters() {
		return playerCharacters;
	}

	public World getWorld() {
		return world;
	}

	public void refresh() {
		// Add every new player character to the characters list
		playerCharacters.addAll(newPlayerCharacters);
		newPlayerCharacters.clear();

		// Add every new entity to the state and physics body
		newEntities.forEach(e -> e.attachToPhysics(this));
		entities.addAll(newEntities);
		newEntities.clear();

		// Think & remove expired entities
		for (Iterator<Entity<?>> e = entities.iterator(); e.hasNext();) {
			Entity<?> entity = e.next();
			entity.think(this);
			if (entity.isExpired(getStateTime())) {
				e.remove();
				playerCharacters.remove(entity);
				world.destroyBody(entity.getBody());
			}
		}
	}

	private float accumulator = 0;
	private static final float TIME_STEP = 1f/45;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;

	public void doPhysicsStep(float deltaTime) {
		// fixed time step
		// max frame time to avoid spiral of death (on slow devices)
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while (accumulator >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			accumulator -= TIME_STEP;
		}
	}
}
