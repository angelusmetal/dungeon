package com.dungeon.engine.util;

import com.dungeon.engine.entity.Entity;

/**
 * Helps find the closest entity to a source entity
 */
public class ClosestEntity {
	private final Entity source;
	private Entity entity;
	private float dst2 = Float.MAX_VALUE;

	private ClosestEntity(Entity source) {
		this.source = source;
	}

	public static ClosestEntity to(Entity source) {
		return new ClosestEntity(source);
	}

	/**
	 * Accept a target entity; if that entity is closer than the currently closest, it will become the new closest
	 * @param target
	 */
	public void accept(Entity target) {
		float targetDistance2 = source.getOrigin().dst2(target.getOrigin());
		if (targetDistance2 < dst2) {
			dst2 = targetDistance2;
			entity = target;
		}
	}

	/**
	 * Combine this collector with another collector
	 * @param target
	 */
	public void combine(ClosestEntity target) {
		if (target.dst2 < dst2) {
			dst2 = target.dst2;
			entity = target.entity;
		}
	}
	public Entity getEntity() {
		return entity;
	}
	public float getDst2() {
		return dst2;
	}
}
