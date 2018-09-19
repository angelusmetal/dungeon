package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class EntityManager {

	private final List<Entity> entities = new LinkedList<>();
	private final List<Entity> newEntities = new LinkedList<>();

	public void add(Entity entity) {
		newEntities.add(entity);
	}

	/** The initial commit skips the spawn method, which can be quite costly for densely populated levels */
	public void initialCommit() {
		// TODO We may be able to remove this if we can make traversing large sets much more efficient
		entities.addAll(newEntities);
		newEntities.clear();
	}

	public void commit() {
		entities.addAll(newEntities);
		List<Entity> spawning = new ArrayList<>(newEntities);
		newEntities.clear();
		spawning.forEach(Entity::spawn);
	}

	public void clear() {
		entities.clear();
	}

	public void process() {
		for (Iterator<Entity> e = entities.iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				e.remove();
			}
		}
	}

	public Stream<Entity> all() {
		return stream();
	}

	public Stream<Entity> point(Vector2 point) {
		return stream().filter(e -> e.collides(point));
	}

	public Stream<Entity> colliding(Entity entity) {
		// TODO is it better to first scan by radius and only then by bounding box?
		return stream().filter(e -> e.collides(entity));
	}

	public Stream<Entity> area(Body area) {
		return stream().filter(e -> e.collides(area));
	}

	/**
	 * @return Stream of entities whose origin is at radius distance or less, from the provided radius
	 */
	public Stream<Entity> radius(Vector2 origin, float radius) {
		float radius2 = Util.length2(radius);
		return stream().filter(e -> e.getOrigin().dst2(origin) <= radius2);
	}

	public <T extends Entity> Stream<T> ofType(Class<T> type) {
		return stream().filter(e -> type.isAssignableFrom(e.getClass())).map(type::cast);
	}

	private Stream<Entity> stream() {
		return entities.stream();
	}

}
