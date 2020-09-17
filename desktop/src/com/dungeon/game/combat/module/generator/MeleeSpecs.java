package com.dungeon.game.combat.module.generator;

import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.StatusEffect;

/**
 * Defines the specs of a projectile in a projectile weapon
 */
class MeleeSpecs {
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
	/** How many things a single attack can hit */
	int hitCount = 1;
	/** how many units (pixel) does this weapon hit */
	int range;
}
