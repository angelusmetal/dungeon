package com.dungeon.game.combat.module;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;

/**
 * Creates a particle, rotated according to aim
 */
public class AimedParticleModule implements WeaponModule {
	final EntityPrototype prototype;
	float spawnDistance = 0;

	public AimedParticleModule(EntityPrototype prototype) {
		this.prototype = prototype;
	}

	public AimedParticleModule spawnDistance(float spawnDistance) {
		this.spawnDistance = spawnDistance;
		return this;
	}

	@Override
	public void apply(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = origin.cpy().mulAdd(aim, spawnDistance);
		Entity entity = new Entity(prototype, hitOrigin);
		entity.setRotation(aim.angle());
		Engine.entities.add(entity);
	}
}
