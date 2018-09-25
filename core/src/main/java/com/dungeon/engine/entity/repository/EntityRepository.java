package com.dungeon.engine.entity.repository;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.util.StopWatch;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityRepository {

	private final List<Entity> dynamic = new LinkedList<>();
	private final List<Entity> newEntities = new LinkedList<>();
	private final List<Entity> staticOnes = new LinkedList<>();
	private QuadTree quadTree;
	public final StopWatch processTime = new StopWatch();

	public void add(Entity entity) {
		newEntities.add(entity);
	}

	public void commit() {
		newEntities.forEach(e -> {
			if (e.isStatic()) {
				staticOnes.add(e);
				quadTree.insert(e);
			} else {
				dynamic.add(e);
			}
		});
		List<Entity> spawning = new ArrayList<>(newEntities);
		newEntities.clear();
		// This takes a lot of time in very densely populated maps
		spawning.forEach(Entity::spawn);
	}

	public void clear(int width, int height) {
		dynamic.clear();
		System.out.println("Creating QuadTree of size: [" + width + ", " + height + "]");
		quadTree = new QuadTree(new Rectangle(0, 0, width, height));
	}

	public void process() {
		processTime.start();
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
				quadTree.remove(entity);
			}
		}

		processTime.stop();
	}

	public Stream<Entity> dynamic() {
		return dynamic.stream();
	}

	public Stream<Entity> point(Vector2 point) {
		return Stream.concat(dynamic(), quadTree.retrieve(point.x, point.x, point.y, point.y));
	}

	public Stream<Entity> colliding(Entity entity) {
		// TODO is it better to first scan by radius and only then by bounding box?
		return inRectStrict(
				entity.getBody().getBottomLeft().x,
				entity.getBody().getTopRight().x,
				entity.getBody().getBottomLeft().y,
				entity.getBody().getTopRight().y);
	}

	public Stream<Entity> area(Body area) {
		return inRectStrict(
				area.getBottomLeft().x,
				area.getTopRight().x,
				area.getBottomLeft().y,
				area.getTopRight().y);
	}

	/**
	 * @return Stream of entities whose origin is at radius distance or less, from the provided radius
	 */
	public Stream<Entity> radius(Vector2 origin, float radius) {
		float radius2 = Util.length2(radius);
		return inRectStrict(
				origin.x - radius,
				origin.x + radius,
				origin.y - radius,
				origin.y + radius)
				.filter(e -> e.getOrigin().dst2(origin) <= radius2);
	}

	public Stream<Entity> inViewPort(ViewPort viewPort) {
		return inRectAprox(
				viewPort.cameraX,
				viewPort.cameraX + viewPort.cameraWidth,
				viewPort.cameraY,
				viewPort.cameraY + viewPort.cameraHeight)
				.filter(viewPort::isInViewPort);
	}

	public Stream<Entity> lightInViewPort(ViewPort viewPort) {
		return inRectAprox(
				viewPort.cameraX - 100,
				viewPort.cameraX + viewPort.cameraWidth + 200,
				viewPort.cameraY - 100,
				viewPort.cameraY + viewPort.cameraHeight + 200)
				.filter(viewPort::lightIsInViewPort);
	}

	/** Aproximation that includes all dynamic entities and entities in partitions that intersect with the rectangle and more */
	public Stream<Entity> inRectAprox(float x1, float x2, float y1, float y2) {
		return Stream.concat(dynamic(), quadTree.retrieve(x1, x2, y1, y2));
	}

	/** Includes all dynamic entities and entities in partitions that intersect with the rectangle */
	public Stream<Entity> inRectStrict(float x1, float x2, float y1, float y2) {
		return Stream.concat(dynamic(), quadTree.retrieve(x1, x2, y1, y2)).filter(e -> e.collides(x1, x2, y1, y2));
	}

	public <T extends Entity> Stream<T> ofType(Class<T> type) {
		return all().filter(e -> type.isAssignableFrom(e.getClass())).map(type::cast);
	}

	public Stream<Entity> all() {
		return Stream.concat(dynamic.stream(), staticOnes.stream());
	}

	public int staticCount() {
		return staticOnes.size();
	}

	public int dynamicCount() {
		return dynamic.size();
	}

	public String analysis() {
		return quadTree.getStats().stream().filter(s -> s.count != 0).map(QuadTree.Stats::toString).collect(Collectors.joining("\n"));
	}

}
