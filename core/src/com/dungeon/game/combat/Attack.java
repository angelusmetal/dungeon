package com.dungeon.game.combat;

public class Attack {
	private final float damage;
	private final DamageType damageType;
	private final float knockback;

	public Attack(float damage, DamageType damageType, float knockback) {
		this.damage = damage;
		this.damageType = damageType;
		this.knockback = knockback;
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
}
