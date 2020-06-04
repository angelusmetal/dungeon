package com.dungeon.game.resource.loader;

import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.level.TilePrototype;
import com.dungeon.game.resource.DungeonResources;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.List;

public class TilePrototypeLoader implements ResourceLoader<TilePrototype> {

	private static final String TYPE = "tile";
	private final ResourceRepository<TilePrototype> repository;

	public TilePrototypeLoader(ResourceRepository<TilePrototype> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<TilePrototype> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config descriptor) {
		List<ResourceIdentifier> dependencies = new ArrayList<>();
		dependencies.add(new ResourceIdentifier("tileset", ConfigUtil.requireString(descriptor, "floor")));
		dependencies.add(new ResourceIdentifier("tileset", ConfigUtil.requireString(descriptor, "wall")));
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), descriptor, dependencies);
	}

	@Override
	public TilePrototype read(String identifier, Config descriptor) {
		return new TilePrototype.Builder()
				.floor(DungeonResources.tilesets.get(ConfigUtil.requireString(descriptor, "floor")))
				.wall(DungeonResources.tilesets.get(ConfigUtil.requireString(descriptor, "wall")))
				.solid(ConfigUtil.requireBoolean(descriptor, "solid"))
				.build();
	}
}
