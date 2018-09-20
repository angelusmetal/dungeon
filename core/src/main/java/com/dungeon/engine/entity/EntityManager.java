package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class EntityManager {

	private final List<Entity> dynamic = new LinkedList<>();
	private final List<Entity> newEntities = new LinkedList<>();
	private final List<Entity> staticOnes = new LinkedList<>();
	private List<List<Entity>> partitions = new ArrayList<>();

	private final int partitionSize;
	private int xPartitions;
	private int yPartitions;
	private int partitionCount;

	public EntityManager(int partitionSize) {
		this.partitionSize = partitionSize;
	}

	public void add(Entity entity) {
		newEntities.add(entity);
	}

	public void commit() {
		newEntities.forEach(e -> {
			if (e.isStatic()) {
				staticOnes.add(e);
				// Find out in which partitions this static entity will be at, for faster lookup
				int left = (int) Util.clamp(e.getBody().getBottomLeft().x / partitionSize, 0, xPartitions - 1);
				int right = (int) Util.clamp(e.getBody().getTopRight().x / partitionSize, 0, xPartitions - 1);
				int bottom = (int) Util.clamp(e.getBody().getBottomLeft().y / partitionSize, 0, yPartitions - 1);
				int top = (int) Util.clamp(e.getBody().getTopRight().y / partitionSize, 0, yPartitions - 1);
				for (int x = left; x <= right; ++x) {
					for (int y = bottom; y <= top; ++y) {
						partitions.get(x * yPartitions + y).add(e);
					}
				}
			} else {
				dynamic.add(e);
			}
		});
		List<Entity> spawning = new ArrayList<>(newEntities);
		newEntities.clear();
		spawning.forEach(Entity::spawn);
	}

	public void clear(int width, int height) {
		dynamic.clear();
		xPartitions = width / partitionSize;
		yPartitions = height / partitionSize;
		partitionCount = xPartitions * yPartitions;
		partitions = new ArrayList<>(partitionCount);
		for (int i = 0; i < partitionCount; ++i) {
			partitions.add(new LinkedList<>());
		}
	}

	public void process() {
		// Process dynamic entities
		for (Iterator<Entity> e = dynamic.iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				e.remove();
			}
		}
		// Process static entities
		for (Iterator<Entity> e = staticOnes.iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				e.remove();
			}
		}
		// Remove expired entities from the partitions
		partitions.forEach(partition -> partition.removeIf(Entity::isExpired));
	}

	public Stream<Entity> dynamic() {
		return dynamic.stream();
	}

	public Stream<Entity> point(Vector2 point) {
		int x = (int) Util.clamp(point.x / partitionSize, 0, xPartitions);
		int y = (int) Util.clamp(point.y / partitionSize, 0, yPartitions);
		return Stream.concat(dynamic(), partitions.get(x * yPartitions + y).stream()).filter(e -> e.collides(point));
	}

	public Stream<Entity> colliding(Entity entity) {
		// TODO is it better to first scan by radius and only then by bounding box?
		return inRect(
				entity.getBody().getBottomLeft().x,
				entity.getBody().getTopRight().x,
				entity.getBody().getBottomLeft().y,
				entity.getBody().getTopRight().y)
				.filter(e -> e.collides(entity));
	}

	public Stream<Entity> area(Body area) {
		return inRect(
				area.getBottomLeft().x,
				area.getTopRight().x,
				area.getBottomLeft().y,
				area.getTopRight().y)
				.filter(e -> e.collides(area));
	}

	/**
	 * @return Stream of entities whose origin is at radius distance or less, from the provided radius
	 */
	public Stream<Entity> radius(Vector2 origin, float radius) {
		float radius2 = Util.length2(radius);
		return inRect(
				origin.x - radius,
				origin.x + radius,
				origin.y - radius,
				origin.y + radius)
				.filter(e -> e.getOrigin().dst2(origin) <= radius2);
	}

	public Stream<Entity> inViewPort(ViewPort viewPort) {
		return inRect(viewPort.cameraX, viewPort.cameraX + viewPort.cameraWidth, viewPort.cameraY, viewPort.cameraY + viewPort.cameraHeight);
	}

	/** Includes all dynamic entities and entities in partitions that intersect with the rectangle */
	public Stream<Entity> inRect(float x1, float x2, float y1, float y2) {
		int left = (int) Util.clamp(x1 / partitionSize, 0, xPartitions - 1);
		int right = (int) Util.clamp(x2 / partitionSize, 0, xPartitions - 1);
		int bottom = (int) Util.clamp(y1 / partitionSize, 0, yPartitions - 1);
		int top = (int) Util.clamp(y2 / partitionSize, 0, yPartitions - 1);
		Stream<Entity> stream = dynamic();
		for (int x = left; x <= right; ++x) {
			for (int y = bottom; y <= top; ++y) {
				stream = Stream.concat(stream, partitions.get(x * yPartitions + y).stream());
			}
		}
		return stream;
	}

	public <T extends Entity> Stream<T> ofType(Class<T> type) {
		return all().filter(e -> type.isAssignableFrom(e.getClass())).map(type::cast);
	}

	public Stream<Entity> all() {
		return Stream.concat(dynamic.stream(), staticOnes.stream());
	}

}
