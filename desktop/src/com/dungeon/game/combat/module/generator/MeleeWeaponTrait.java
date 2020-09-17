package com.dungeon.game.combat.module.generator;

import java.util.function.BiConsumer;

/**
 * Defines a projectile trait that can possibly be applied to a projectile specs
 */
class MeleeWeaponTrait {
	/** Score cost of this trait; gets subtracted/added after applying the trait */
	int cost;
	/** Gold cost of this trait; multiplies cost after applying the trait */
	float goldCost;
	/** If present, cannot coexist with any other trait with the same unique label */
	String uniqueLabel;
	/** Actual logic that modifies a projectile */
	BiConsumer<WeaponSpecs, MeleeSpecs> modifier;
	MeleeWeaponTrait(int cost, float goldCost, String uniqueLabel, BiConsumer<WeaponSpecs, MeleeSpecs> modifier) {
		this.cost = cost;
		this.goldCost = goldCost;
		this.uniqueLabel = uniqueLabel;
		this.modifier = modifier;
	}
}
