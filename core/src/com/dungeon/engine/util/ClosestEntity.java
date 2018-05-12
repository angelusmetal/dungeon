package com.dungeon.engine.util;

import com.dungeon.engine.entity.Entity;

public class ClosestEntity {
	private Entity source;
	private Entity entity;
	private float dst2 = Float.MAX_VALUE;
	public ClosestEntity(Entity source) {
		this.source = source;
	}
	public void accept(Entity target) {
		float targetDistance2 = source.getPos().dst2(target.getPos());
		if (targetDistance2 < dst2) {
			dst2 = targetDistance2;
			entity = target;
		}
	}
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
