package com.dungeon.game.combat.module.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.StatusEffect;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.combat.module.ArcAttackModule;
import com.dungeon.game.combat.module.ModularWeapon;
import com.dungeon.game.combat.module.SoundModule;
import com.dungeon.game.combat.module.WeaponModule;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.DungeonResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StaffWeaponGenerator {

	private final List<String> staffSkins = Arrays.asList(
			"weapon_fire_wand", "weapon_fire_staff", "weapon_fire_scepter", "weapon_poison_wand", "weapon_poison_staff", "weapon_poison_scepter"
	);

	private final List<ProjectileWeaponTrait> projectileTraits = Arrays.asList(
			// Slight base damage
			new ProjectileWeaponTrait(2, 1.2f, null, (weapon, projectile) -> {
				projectile.baseDamage += 2;
			}),
			// Moderate base damage
			new ProjectileWeaponTrait(10, 1.2f, null, (weapon, projectile) -> {
				projectile.baseDamage += 5;
			}),
			// Large base damage
			new ProjectileWeaponTrait(25, 1.2f, null, (weapon, projectile) -> {
				projectile.baseDamage += 10;
			}),
			// Slight spread damage
			new ProjectileWeaponTrait(1, 1.2f, null, (weapon, projectile) -> {
				projectile.spreadDamage += 1;
			}),
			// Moderate spread damage
			new ProjectileWeaponTrait(5, 1.2f, null, (weapon, projectile) -> {
				projectile.spreadDamage += 3;
			}),
			// Large spread damage
			new ProjectileWeaponTrait(12, 1.2f, null, (weapon, projectile) -> {
				projectile.spreadDamage += 5;
			}),
			// Oscillate
			new ProjectileWeaponTrait(20, 1.2f, "steer", (weapon, projectile) -> {
				projectile.oscillate = true;
			}),
			// Heatseeker
			new ProjectileWeaponTrait(15, 1.2f, "steer", (weapon, projectile) -> {
				projectile.heatseeker = true;
			}),
			// Bounce
			new ProjectileWeaponTrait(15, 1.2f, "steer", (weapon, projectile) -> {
				projectile.bounce = true;
			}),
			// Slight speed boost
			new ProjectileWeaponTrait(5, 1.2f, "speed", (weapon, projectile) -> {
				projectile.speed *= 1.1f;
			}),
			// Moderate speed boost
			new ProjectileWeaponTrait(10, 1.2f, "speed", (weapon, projectile) -> {
				projectile.speed *= 1.3f;
			}),
			// Large speed boost
			new ProjectileWeaponTrait(25, 1.2f, "speed", (weapon, projectile) -> {
				projectile.speed *= 1.5f;
			}),
//			// Medium size
//			new ProjectileWeaponTrait(10, 1.2f, "size", (weapon, projectile) -> {
//				projectile.size *= 1.2f;
//			}),
//			// Large size
//			new ProjectileWeaponTrait(20, 1.2f, "size", (weapon, projectile) -> {
//				projectile.size *= 1.8f;
//			}),
//			// Giant size
//			new ProjectileWeaponTrait(30, 1.2f, "size", (weapon, projectile) -> {
//				projectile.size *= 2.5f;
//			}),
			// Fire type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.BURN;
				projectile.statusChance = 0.2f;
			}),
			// Fire type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.BURN;
				projectile.statusChance = 0.2f;
			}),
			// Poison type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.POISON;
				projectile.statusChance = 0.2f;
			}),
			// Chill type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.CHILL;
				projectile.statusChance = 0.2f;
			}),
			// Freeze type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.FROZEN;
				projectile.statusChance = 0.2f;
			}),
			// Lightning type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.LIGHTNING;
				projectile.statusChance = 0.2f;
			}),
			// Life steal type
			new ProjectileWeaponTrait(15, 1.2f, "damageType", (weapon, projectile) -> {
				projectile.damageType = DamageType.ELEMENTAL;
				projectile.statusEffect = StatusEffect.LIFE_STEAL;
				projectile.statusChance = 0.2f;
			}),
			// Attack speed
			new ProjectileWeaponTrait(10, 1.2f, "damageType", (weapon, projectile) -> {
				weapon.cooldown *= 0.9f;
			}),
			// Stamina consumption
			new ProjectileWeaponTrait(10, 1.2f, "damageType", (weapon, projectile) -> {
				weapon.energyDrain *= 0.9f;
			})
	);

	public Weapon generate(int score) {
		WeaponSpecs weapon = new WeaponSpecs();
		ProjectileSpecs projectile = new ProjectileSpecs();
		int remaining = score;
		Set<String> unique = new HashSet<>();
		// Copy base traits
		LinkedList<ProjectileWeaponTrait> traits = new LinkedList<>(projectileTraits);

		// Purchase as many traits as possible with the remaining score
		while (remaining > 0 && !traits.isEmpty()) {
			ProjectileWeaponTrait trait = traits.remove(Rand.nextInt(traits.size()));
			// Cannot afford
			if (trait.cost > remaining) {
				continue;
			}
			// Has a unique label that has already been picked up
			if (trait.uniqueLabel != null && unique.contains(trait.uniqueLabel)) {
				continue;
			}

			trait.modifier.accept(weapon, projectile);
			remaining -= trait.cost;
			weapon.goldCost *= trait.goldCost;

			if (trait.uniqueLabel != null) {
				// Track unique label
				unique.add(trait.uniqueLabel);
			} else {
				// If it has no unique label, it can be reused, so add it back
				traits.add(trait);
			}
		}

		// Finally, build a weapon based off the traits
		List<WeaponModule> modules = new ArrayList<>();

		// Add attack sound
		modules.add(new SoundModule(Resources.sounds.get("audio/sound/magic_bolt.ogg")));

		// Create projectile prototype
		EntityPrototype projectilePrototype;
		StringBuilder name = new StringBuilder();
		if (projectile.statusEffect == StatusEffect.BURN) {
			projectilePrototype = DungeonResources.prototypes.get("projectile_fire");
			name.append("Molten ");
		} else if (projectile.statusEffect == StatusEffect.LIGHTNING) {
			projectilePrototype = DungeonResources.prototypes.get("projectile_lightning");
			name.append("Charged ");
		} else if (projectile.statusEffect == StatusEffect.FROZEN) {
			projectilePrototype = DungeonResources.prototypes.get("projectile_ice");
			name.append("Glacial ");
		} else if (projectile.statusEffect == StatusEffect.CHILL) {
			projectilePrototype = DungeonResources.prototypes.get("projectile_chill");
			name.append("Chilling ");
		} else if (projectile.statusEffect == StatusEffect.POISON) {
			projectilePrototype = DungeonResources.prototypes.get("projectile_venom");
			name.append("Venomous ");
		} else if (projectile.statusEffect == StatusEffect.LIFE_STEAL) {
			projectilePrototype = DungeonResources.prototypes.get("projectile_lifesteal");
			name.append("Vampiric ");
		} else {
			projectilePrototype = DungeonResources.prototypes.get("projectile_cat");
			name.append("Earthen ");
		}

		if (projectile.baseDamage < 3) {
			name.append("wand");
		} else if (projectile.baseDamage < 10) {
			name.append("staff");
		} else if (projectile.baseDamage < 20) {
			name.append("scepter");
		} else {
			name.append("warstaff");
		}

		name.append(" of the ");
		// Bonciness
		if (projectile.bounce) {
			projectilePrototype.bounciness(1f);
			name.append("mirroring ");
		}

		// Heat seeker
		if (projectile.heatseeker) {
			projectilePrototype.with(Traits.autoSeek(0.1f, 60, () -> Engine.entities.dynamic().filter(PlayerEntity.TARGET_NON_PLAYER_CHARACTERS)));
			name.append("reflecting ");
		}

		// Speed
		projectilePrototype.speed(200 * projectile.speed);
		projectilePrototype.drawScale(new Vector2(projectile.size, projectile.size));

		if (projectile.size > 1) {
			name.append("hulking ");
		}
		else if (projectile.speed > 1) {
			name.append("hasty ");
		}

		// Oscillation means adding twice the projectiles with mirrored oscillation
		if (!projectile.oscillate) {
			modules.add(new ArcAttackModule(projectilePrototype, projectile.damageType, projectile.baseDamage, projectile.baseDamage + projectile.spreadDamage, projectile.count, 10));
			name.append("hare");
		} else {
			EntityPrototype mirror = new EntityPrototype(projectilePrototype);
			mirror.with(Traits.movOscillate(2, -15));
			modules.add(new ArcAttackModule(mirror, projectile.damageType, projectile.baseDamage, projectile.baseDamage + projectile.spreadDamage, projectile.count, 10));
			projectilePrototype.with(Traits.movOscillate(2, 15));
			modules.add(new ArcAttackModule(projectilePrototype, projectile.damageType, projectile.baseDamage, projectile.baseDamage + projectile.spreadDamage, projectile.count, 10));
			name.append("serpent");
		}

		return new ModularWeapon(name.toString(), Resources.animations.get(Rand.pick(staffSkins)), modules, weapon.cooldown, weapon.energyDrain, (int) weapon.goldCost, Color.WHITE);
	}

}
