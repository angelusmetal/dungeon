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
	private QuadTree quadTree;
	public final StopWatch processTime = new StopWatch();
	private int staticCount = 0;

	public void add(Entity entity) {
		newEntities.add(entity);
	}

	public void commit(boolean checkSpawn) {
		newEntities.forEach(e -> {
			if (e.isStatic()) {
				staticCount++;
				quadTree.insert(e);
			} else {
				dynamic.add(e);
			}
		});
		if (checkSpawn) {
			List<Entity> spawning = new ArrayList<>(newEntities);
			// This takes a lot of time in very densely populated maps
			spawning.forEach(Entity::spawn);
		}
		newEntities.clear();
	}

	public void clear(int width, int height) {
		dynamic.clear();
		System.out.println("Creating QuadTree of size: [" + width + ", " + height + "]");
		quadTree = new QuadTree(new Rectangle(0, 0, width, height));
	}

//	public void updateAll() {
//		processTime.start();
//		// Process dynamic entities
//		for (Iterator<Entity> e = dynamic.iterator(); e.hasNext();) {
//			Entity entity = e.next();
//			entity.doThink();
//			entity.move();
//			if (entity.isExpired()) {
//				e.remove();
//			}
//		}
//		// Process static entities
//		for (Iterator<Entity> e = staticOnes.iterator(); e.hasNext();) {
//			Entity entity = e.next();
//			entity.doThink();
//			entity.move();
//			if (entity.isExpired()) {
//				e.remove();
//				quadTree.remove(entity);
//			}
//		}
//
//		processTime.stop();
//	}

	public void update(Stream<Entity> stream) {
		processTime.start();
		// Process static entities in stream
		LinkedList<Entity> removed = new LinkedList<>();
		stream.filter(Entity::isStatic).forEach(entity -> {
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				removed.add(entity);
				staticCount--;
			}
		});
		removed.forEach(quadTree::remove);

		// Process dynamic entities
		for (Iterator<Entity> e = dynamic.iterator(); e.hasNext();) {
			Entity entity = e.next();
			entity.doThink();
			entity.move();
			if (entity.isExpired()) {
				e.remove();
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
				viewPort.cameraY + viewPort.cameraHeight);
	}

	public Stream<Entity> inViewPort(ViewPort viewPort, float margin) {
		return inRectAprox(
				viewPort.cameraX - margin,
				viewPort.cameraX + viewPort.cameraWidth + margin + margin,
				viewPort.cameraY - margin,
				viewPort.cameraY + viewPort.cameraHeight + margin + margin);
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
		return dynamic().filter(e -> type.isAssignableFrom(e.getClass())).map(type::cast);
	}

	public int staticCount() {
		return staticCount;
	}

	public int dynamicCount() {
		return dynamic.size();
	}

	public String analysis() {
		return quadTree.getStats().stream().filter(s -> s.count != 0).map(QuadTree.Stats::toString).collect(Collectors.joining("\n"));
	}

}
