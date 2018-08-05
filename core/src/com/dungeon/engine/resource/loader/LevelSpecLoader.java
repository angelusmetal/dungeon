package com.dungeon.engine.resource.loader;

import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.tileset.LevelSpec;
import com.dungeon.game.tileset.Tileset;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LevelSpecLoader implements ResourceLoader<LevelSpec> {

	private static final String TYPE = "level";

	private final Map<String, LevelSpec> repository;

	public LevelSpecLoader(Map<String, LevelSpec> repository) {
		this.repository = repository;
	}

	@Override
	public Map<String, LevelSpec> getRepository() {
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
	public LevelSpec read(Toml toml) {
		Tileset tileset = ResourceManager.getTileset(ConfigUtil.requireString(toml, "tileset"));
		Color lightColor = ConfigUtil.requireColor(toml, "light");
		List<RoomPrototype> rooms = ConfigUtil.<String>requireList(toml, "rooms").stream().map(ResourceManager::getRoomPrototype).collect(Collectors.toList());
		List<EntityType> monsters = ConfigUtil.<String>requireList(toml, "monsters").stream().map(EntityType::valueOf).collect(Collectors.toList());
		return new LevelSpec(tileset, () -> lightColor, rooms, monsters);
	}

}
