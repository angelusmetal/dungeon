package com.dungeon.game.resource;

import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TilePrototype;
import com.dungeon.game.resource.loader.EntityPrototypeLoader;
import com.dungeon.game.resource.loader.EnvironmentLoader;
import com.dungeon.game.resource.loader.RoomPrototypeLoader;
import com.dungeon.game.resource.loader.TilePrototypeLoader;
import com.dungeon.game.resource.loader.TilesetLoader;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.Tileset;

/**
 * Repositories & loaders for Dungeon-specific resources
 */
public class DungeonResources {
	public static final String DEFAULT_FONT = "alegreya-sans-sc-9";

	public static final ResourceRepository<EntityPrototype> prototypes = new ResourceRepository<>();
	public static final ResourceRepository<Tileset> tilesets = new ResourceRepository<>();
	public static final ResourceRepository<TilePrototype> tiles = new ResourceRepository<>();
	public static final ResourceRepository<RoomPrototype> rooms = new ResourceRepository<>();
	public static final ResourceRepository<Environment> environments = new ResourceRepository<>();

	public static void addLoaders(){
		Resources.loader.registerLoader("prototype", new EntityPrototypeLoader(prototypes));
		Resources.loader.registerLoader("tileset", new TilesetLoader(tilesets));
		Resources.loader.registerLoader("tile", new TilePrototypeLoader(tiles));
		Resources.loader.registerLoader("room", new RoomPrototypeLoader(rooms));
		Resources.loader.registerLoader("environment", new EnvironmentLoader(environments));
	}

}
