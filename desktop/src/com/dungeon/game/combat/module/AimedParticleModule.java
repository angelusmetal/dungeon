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

	/**
	 * Create a weapon module that spawns a particle rotated according to aim vector
	 * @param prototype Entity prototype for the particle.
	 */
	public AimedParticleModule(EntityPrototype prototype) {
		this.prototype = prototype;
	}

	/**
	 * Set spawn distance, which indicates how far from origin (into aim direction) will the particle spawn.
	 */
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
