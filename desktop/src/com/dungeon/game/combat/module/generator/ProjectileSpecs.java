package com.dungeon.game.combat.module.generator;

import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.StatusEffect;

/**
 * Defines the specs of a projectile in a projectile weapon
 */
class ProjectileSpecs {
	/** Minimum damage */
	float baseDamage = 1;
	/** How much the damage can spread above base damage */
	float spreadDamage = 0;
	/** Damage type */
	DamageType damageType;
	/** Status effect */
	StatusEffect statusEffect;
	/** Chance of applying status */
	float statusChance;
	/** Projectile count  */
	int count = 1;
	/** Whether projectiles oscillate */
	boolean oscillate;
	/** Whether projectiles seek targets */
	boolean heatseeker;
	/** Whether projectiles bounce */
	boolean bounce;
	/** Projectile speed */
	float speed = 1f;
	/** Projectile size */
	float size = 1f;
}
