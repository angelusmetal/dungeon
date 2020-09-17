package com.dungeon.game.combat.module.generator;

import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.Weapon;

public class ModularWeaponGenerator {

	private final SwordWeaponGenerator swordGenerator = new SwordWeaponGenerator();
	private final StaffWeaponGenerator staffGenerator = new StaffWeaponGenerator();

	public Weapon generate(int score) {
		if (Rand.chance(0.5f)) {
		return swordGenerator.generate(score);
		} else {
			return staffGenerator.generate(score);
		}
	}


}
