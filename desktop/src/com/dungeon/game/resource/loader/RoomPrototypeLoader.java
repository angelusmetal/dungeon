package com.dungeon.game.resource.loader;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.resource.ResourceDescriptor;
import com.dungeon.engine.resource.ResourceIdentifier;
import com.dungeon.engine.resource.ResourceLoader;
import com.dungeon.engine.resource.ResourceRepository;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.level.ProceduralLevelGenerator;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TileType;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoomPrototypeLoader implements ResourceLoader<RoomPrototype> {

	private static final String TYPE = "room";

	private final ResourceRepository<RoomPrototype> repository;

	public RoomPrototypeLoader(ResourceRepository<RoomPrototype> repository) {
		this.repository = repository;
	}

	@Override
	public ResourceRepository<RoomPrototype> getRepository() {
		return repository;
	}

	@Override
	public ResourceDescriptor scan(String key, Config config) {
		List<ResourceIdentifier> dependencies = new ArrayList<>();
		getPlaceholders(config).stream().map(EntityPlaceholder::getType).forEach(prototype -> dependencies.add(new ResourceIdentifier("prototype", prototype)));
		return new ResourceDescriptor(new ResourceIdentifier(TYPE, key), config, Collections.emptyList());
	}

	@Override
	public RoomPrototype read(Config config) {
		return new RoomPrototype(getTiles(config), getConnections(config), getSpawns(config), getPlaceholders(config));
	}

	private static TileType[][] getTiles(Config config) {
		Vector2 size = ConfigUtil.requireVector2(config, "size");
		List<String> tiles = ConfigUtil.requireStringList(config, "tiles");
		if (tiles.size() == 0) {
			throw new RuntimeException("'tiles' cannot be empty");
		}
		int width = (int) size.x;
		int height = (int) size.y;
//		int x = 0, y = 0;
		if (width * height != tiles.size()) {
			throw new RuntimeException("'tiles' must have as many elements as size indicates, but has " + tiles.size() + " instead of " + (width * height));
		}

//		TileType[][] array = new TileType[width][];
//		for (String tile : tiles) {
//			array[x] = new
//		}
//
		// Invert coordinates
		TileType[][] array = new TileType[width][];
		for (int x = 0; x < width; ++x) {
			array[x] = new TileType[height];
			for (int y = 0; y < height; ++y) {
				array[x][height - y - 1] = TileType.valueOf(tiles.get(y * width + x).toUpperCase());
			}
		}
		return array;
	}

	private static List<ProceduralLevelGenerator.ConnectionPoint> getConnections(Config config) {
		return config.getConfigList("connections").stream().map(t -> {
			int x = ConfigUtil.requireInteger(t, "x");
			int y = ConfigUtil.requireInteger(t, "y");
			String type = ConfigUtil.requireString(t, "type");
			ProceduralLevelGenerator.Direction direction = ProceduralLevelGenerator.Direction.valueOf(type);
			return new ProceduralLevelGenerator.ConnectionPoint(x, y, direction);
		}).collect(Collectors.toList());
	}

	private static List<Vector2> getSpawns(Config config) {
		return config.getConfigList("spawns").stream().map(t -> {
			float x = ConfigUtil.requireFloat(t, "x");
			float y = ConfigUtil.requireFloat(t, "y");
			return new Vector2(x, y);
		}).collect(Collectors.toList());
	}

	private static List<EntityPlaceholder> getPlaceholders(Config config) {
		return config.getConfigList("placeholders").stream().map(t -> {
			float x = ConfigUtil.requireFloat(t, "x");
			float y = ConfigUtil.requireFloat(t, "y");
			float chance = ConfigUtil.getFloat(t, "chance").orElse(1f);
			String type = ConfigUtil.requireString(t, "type");
			return new EntityPlaceholder(type, new Vector2(x, y), chance);
		}).collect(Collectors.toList());
	}

}