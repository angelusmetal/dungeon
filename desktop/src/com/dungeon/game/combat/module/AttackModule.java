package com.dungeon.game.combat.module;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.Projectile;

import java.util.function.Function;

/**
 * Creates an attack entity
 */
public class AttackModule implements WeaponModule {
	final EntityPrototype prototype;
	final DamageType damageType;
	final float minDps;
	final float maxDps;
	final Function<Entity, Attack> attackFunction;
	final float knockback = 0;
	float spawnDistance = 0;

	public AttackModule(EntityPrototype prototype, DamageType damageType, float minDps, float maxDps) {
		this.prototype = prototype;
		this.damageType = damageType;
		this.minDps = minDps;
		this.maxDps = maxDps;
		attackFunction = emitter -> new Attack(emitter, (int) (Rand.between(minDps, maxDps) + 0.5f), damageType, knockback);
	}

	public AttackModule spawnDistance(float spawnDistance) {
		this.spawnDistance = spawnDistance;
		return this;
	}

	@Override
	public void apply(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = origin.cpy().mulAdd(aim, spawnDistance);
		Projectile projectile = new Projectile(hitOrigin, prototype, attackFunction);
		projectile.impulse(aim.cpy().setLength(projectile.getSpeed()));
		projectile.spawn();
		Engine.entities.add(projectile);
	}
}
