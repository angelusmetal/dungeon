package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

import java.util.function.Supplier;

public abstract class Weapon {
	private Supplier<Float> damage;
	private DamageType damageType;
	private float knockback;

	public Weapon(Supplier<Float> damage, DamageType damageType, float knockback) {
		this.damage = damage;
		this.damageType = damageType;
		this.knockback = knockback;
	}

	/**
	 * Create the required entities to spawn an attack in the world
	 */
	public abstract void spawnEntities(Vector2 origin, Vector2 aim);

	/**
	 * @return A newly created {@link Attack} instance
	 */
	public Attack createAttack() {
		return new Attack(damage.get(), damageType, knockback);
	}

	protected Vector2 shift(Vector2 origin, Vector2 direction, float distance) {
		return origin.cpy().mulAdd(direction, distance);
	}

	protected void impulse(Entity projectile, Vector2 direction) {
		projectile.impulse(direction.cpy().setLength(projectile.getSpeed()));
	}
}
