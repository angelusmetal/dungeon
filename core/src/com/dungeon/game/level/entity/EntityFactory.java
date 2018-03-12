package com.dungeon.game.level.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.state.GameState;

import java.util.EnumMap;

/**
 * Centralizes entity factories. A factory can be registered for each entity type and then an entity can be built for
 * each entity type as well, using the previously registered factories.
 */
public class EntityFactory {

	public interface EntityTypeFactory {
		Entity<?> build(Vector2 origin);
	}

	private final EnumMap<EntityType, EntityTypeFactory> factories = new EnumMap<>(EntityType.class);

	public void registerFactory(EntityType type, EntityTypeFactory factory) {
		factories.put(type, factory);
	}

	public Entity<?> build(EntityType type, Vector2 position) {
		return factories.get(type).build(position);
	}
}
