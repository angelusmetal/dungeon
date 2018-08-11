package com.dungeon.engine.entity.factory;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewEntityFactory {
	private final Map<String, Integer> ordinals = new HashMap<>();
	private final List<NewEntityTypeFactory> factories = new ArrayList<>();

	public int registerFactory(String type, NewEntityTypeFactory factory) {
		int ordinal = factories.size();
		Integer duplicate = ordinals.put(type, ordinal);
		if (duplicate != null) {
			throw new RuntimeException("Entity type '" + type + "' already had a factory associated");
		}
		factories.add(factory);
		return ordinal;
	}

	public int getFactoryOrdinal(String type) {
		Integer ordinal = ordinals.get(type);
		if (ordinal == null) {
			throw new RuntimeException("No factory registered for entity type " + type);
		}
		return ordinal;
	}

	public Entity build(int ordinal, Vector2 origin) {
		if (ordinal < 0 || ordinal >= factories.size()) {
			throw new RuntimeException("No factory registered for ordinal " + ordinal);
		}
		return factories.get(ordinal).build(origin);
	}
}
