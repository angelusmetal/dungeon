package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.util.Util;
import com.dungeon.game.state.GameState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class EntityManager {

	private final List<Entity> entities = new LinkedList<>();
	private final List<Entity> newEntities = new LinkedList<>();

	// TODO Do we really need this?
	private static List<PlayerEntity> playerCharacters = new LinkedList<>();
	private static List<PlayerEntity> newPlayerCharacters = new LinkedList<>();

	public void add(Entity entity) {
		newEntities.add(entity);
		if (entity instanceof PlayerEntity) {
			newPlayerCharacters.add((PlayerEntity) entity);
		}
	}

	public void commit() {
		entities.addAll(newEntities);
		List<Entity> spawning = new ArrayList<>(newEntities);
		newEntities.clear();
		spawning.forEach(Entity::spawn);
		// TODO Does this belong here?
		playerCharacters.addAll(newPlayerCharacters);
		newPlayerCharacters.clear();
	}

	public void clear() {
		entities.clear();
		playerCharacters.clear();
	}

	public void process() {
		for (Iterator<Entity> e = entities.iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				e.remove();
				if (entity instanceof PlayerEntity) {
					playerCharacters.remove(entity);
				}
			}
		}
	}

	public Stream<Entity> all() {
		return entities.stream();
	}

	public Stream<Entity> point(Vector2 point) {
		return entities.stream().filter(e -> e.collides(point));
	}

	public Stream<Entity> colliding(Entity entity) {
		// TODO is it better to first scan by radius and only then by bounding box?
		return entities.stream().filter(e -> e.collides(entity));
	}

	public Stream<Entity> area(Body area) {
		return entities.stream().filter(e -> e.collides(area));
	}

	/**
	 * @return Stream of entities whose origin is at radius distance or less, from the provided radius
	 */
	public Stream<Entity> radius(Vector2 origin, float radius) {
		float radius2 = Util.length2(radius);
		return entities.stream().filter(e -> e.getOrigin().dst2(origin) <= radius2);
	}

	public Stream<PlayerEntity> playerCharacters() {
		return playerCharacters.stream();
	}

	public int playersAlive() {
		return playerCharacters.size() + newPlayerCharacters.size();
	}

}
