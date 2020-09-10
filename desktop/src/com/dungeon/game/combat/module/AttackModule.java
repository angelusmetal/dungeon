package com.dungeon.game.combat.module;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.Projectile;

import java.util.function.Function;

/**
 * Creates an attack projectile
 */
public class AttackModule implements WeaponModule {
	final EntityPrototype prototype;
	final EntityPrototype prototypeHit;
	final DamageType damageType;
	final float minDamage;
	final float maxDamage;
	final Function<Entity, Attack> attackFunction;
	final float knockback = 0;
	float spawnDistance = 0;

	/**
	 * Create a weapon module that spawns a projectile
	 * @param prototype Entity prototype for this projectile
	 * @param damageType Damage type for this projectile
	 * @param minDamage Minimum damage upon hit
	 * @param maxDamage Maximum damage upon hit
	 */
	public AttackModule(EntityPrototype prototype, DamageType damageType, float minDamage, float maxDamage) {
		this.prototype = prototype;
		this.prototypeHit = null;
		this.damageType = damageType;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		attackFunction = emitter -> new Attack(emitter, (int) (Rand.between(minDamage, maxDamage) + 0.5f), damageType, knockback);
	}

	/**
	 * Create a weapon module that spawns a projectile
	 * @param prototype Entity prototype for this projectile
	 * @param damageType Damage type for this projectile
	 * @param minDamage Minimum damage upon hit
	 * @param maxDamage Maximum damage upon hit
	 */
	public AttackModule(EntityPrototype prototype, EntityPrototype prototypeHit, DamageType damageType, float minDamage, float maxDamage) {
		this.prototype = prototype;
		this.prototypeHit = prototypeHit;
		this.damageType = damageType;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		attackFunction = emitter -> new Attack(emitter, (int) (Rand.between(minDamage, maxDamage) + 0.5f), damageType, knockback);
	}

	/**
	 * Set spawn distance, which indicates how far from origin (into aim direction) will the particle spawn.
	 */
	public AttackModule spawnDistance(float spawnDistance) {
		this.spawnDistance = spawnDistance;
		return this;
	}

	@Override
	public void apply(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = origin.cpy().mulAdd(aim, spawnDistance);
		Projectile projectile;
		if (prototypeHit != null) {
			projectile = new Projectile(hitOrigin, prototype, attackFunction) {
				@Override
				protected boolean onEntityCollision(DungeonEntity entity) {
					boolean hit = super.onEntityCollision(entity);
					if (hit) {
						Engine.entities.add(new Entity(prototypeHit, entity.getOrigin()));
					}
					return hit;
				}
			};
		} else {
			projectile = new Projectile(hitOrigin, prototype, attackFunction);
		}
		projectile.impulse(aim.cpy().setLength(projectile.getSpeed()));
		projectile.spawn();
		Engine.entities.add(projectile);
	}
}
