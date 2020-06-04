package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TilePrototype;
import com.dungeon.game.resource.DungeonResources;
import com.dungeon.game.tileset.Environment;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentLoader implements ResourceLoader<Environment> {

	private static final String TYPE = "environment";

	private final ResourceRepository<Environment> repository;

	public EnvironmentLoader(ResourceRepository<Environment> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<Environment> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config config) {
		List<ResourceIdentifier> dependencies = new ArrayList<>();
		dependencies.add(new ResourceIdentifier("tileset", ConfigUtil.requireString(config, "fillTile")));
		dependencies.add(new ResourceIdentifier("tileset", ConfigUtil.requireString(config, "voidTile")));
		ConfigUtil.requireStringList(config, "rooms").forEach(room -> dependencies.add(new ResourceIdentifier("room", room)));
		ConfigUtil.requireStringList(config, "monsters").forEach(monster -> dependencies.add(new ResourceIdentifier("prototype", monster)));
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), config, dependencies);
	}

	@Override
	public Environment read(String identifier, Config config) {
		int tilesize = ConfigUtil.requireInteger(config, "tilesize");
		TilePrototype fillTile = DungeonResources.tiles.get(ConfigUtil.requireString(config, "fillTile"));
		TilePrototype voidTile = DungeonResources.tiles.get(ConfigUtil.requireString(config, "voidTile"));
		Color lightColor = ConfigUtil.requireColor(config, "light");
		List<RoomPrototype> rooms = ConfigUtil.requireStringList(config, "rooms").stream().map(DungeonResources.rooms::get).collect(Collectors.toList());
		List<String> monsters = ConfigUtil.requireStringList(config, "monsters");
		return new Environment(tilesize, fillTile, voidTile, () -> lightColor, rooms, monsters);
	}

}
