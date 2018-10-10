package com.dungeon.game.combat;

import com.dungeon.engine.entity.Entity;

public class Attack {
	private final Entity emitter;
	private final float damage;
	private final DamageType damageType;
	private final float knockback;

	public Attack(Entity emitter, float damage, DamageType damageType, float knockback) {
		this.emitter = emitter;
		this.damage = damage;
		this.damageType = damageType;
		this.knockback = knockback;
	}

	public Entity getEmitter() {
		return emitter;
	}

	public float getDamage() {
		return damage;
	}

	public DamageType getDamageType() {
		return damageType;
	}

	public float getKnockback() {
		return knockback;
	}

	@Override
	public String toString() {
		return "Attack{" +
				"emitter=" + emitter +
				", damage=" + damage +
				", damageType=" + damageType +
				", knockback=" + knockback +
				'}';
	}
}
