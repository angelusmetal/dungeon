package com.dungeon.engine.entity.factory;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EntityFactory {
	private final Map<String, Integer> ordinals = new HashMap<>();
	private final List<EntityTypeFactory> factories = new ArrayList<>();

	public int registerFactory(String type, EntityTypeFactory factory) {
		int ordinal = factories.size();
		Integer duplicate = ordinals.put(type, ordinal);
		if (duplicate != null) {
			throw new RuntimeException("Entity type '" + type + "' already had a factory associated");
		}
		factories.add(factory);
		return ordinal;
	}

	public Entity build(int ordinal, Vector2 origin) {
		if (ordinal < 0 || ordinal >= factories.size()) {
			throw new RuntimeException("No factory registered for ordinal " + ordinal);
		}
		return factories.get(ordinal).build(origin);
	}

	public Entity build(String type, Vector2 origin) {
		return getFactory(type).build(origin);
	}

	public EntityTypeFactory getFactory(String type) {
		Integer ordinal = ordinals.get(type);
		if (ordinal == null) {
			throw new RuntimeException("No factory registered for entity type " + type);
		}
		return factories.get(ordinal);
	}

	public Stream<String> knownTypes() {
		return ordinals.keySet().stream();
	}

}
