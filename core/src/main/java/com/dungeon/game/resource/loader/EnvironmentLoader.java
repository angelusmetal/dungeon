package com.dungeon.game.resource.loader;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.Tileset;
import com.moandjiezana.toml.Toml;

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
	public ResourceDescriptor scan(String key, Toml toml) {
		List<ResourceIdentifier> dependencies = new ArrayList<>();
		dependencies.add(new ResourceIdentifier("tileset", ConfigUtil.requireString(toml, "tileset")));
		ConfigUtil.<String>requireList(toml, "rooms").stream().forEach(room -> dependencies.add(new ResourceIdentifier("room", room)));
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), toml, dependencies);
	}

	@Override
	public Environment read(Toml toml) {
		Tileset tileset = Resources.tilesets.get(ConfigUtil.requireString(toml, "tileset"));
		Color lightColor = ConfigUtil.requireColor(toml, "light");
		List<RoomPrototype> rooms = ConfigUtil.<String>requireList(toml, "rooms").stream().map(Resources.rooms::get).collect(Collectors.toList());
		List<EntityType> monsters = ConfigUtil.<String>requireList(toml, "monsters").stream().map(EntityType::valueOf).collect(Collectors.toList());
		return new Environment(tileset, () -> lightColor, rooms, monsters);
	}

}
