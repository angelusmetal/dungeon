package com.dungeon.game.combat.module;

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
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.DungeonResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class ModularWeaponGenerator {

	/**
	 * Defines the general specs of a weapon
	 */
	static class WeaponSpecs {
		float cooldown = 0.2f;
		float energyDrain = 20f;
		float goldCost = 50f;
	}

	/**
	 * Defines the specs of a projectile in a projectile weapon
	 */
	static class ProjectileSpecs {
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

	/**
	 * Defines a projectile trait that can possibly be applied to a projectile specs
	 */
	static class ProjectileWeaponTrait {
		/** Score cost of this trait; gets subtracted/added after applying the trait */
		int cost;
		/** Gold cost of this trait; multiplies cost after applying the trait */
		float goldCost;
		/** If present, cannot coexist with any other trait with the same unique label */
		String uniqueLabel;
		/** Actual logic that modifies a projectile */
		BiConsumer<WeaponSpecs, ProjectileSpecs> modifier;
		ProjectileWeaponTrait(int cost, float goldCost, String uniqueLabel, BiConsumer<WeaponSpecs, ProjectileSpecs> modifier) {
			this.cost = cost;
			this.goldCost = goldCost;
			this.uniqueLabel = uniqueLabel;
			this.modifier = modifier;
		}
	}

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

	private final List<String> staffSkins = Arrays.asList(
			"weapon_fire_wand", "weapon_fire_staff", "weapon_fire_scepter", "weapon_poison_wand", "weapon_poison_staff", "weapon_poison_scepter"
	);

	/**
	 * Defines the specs of a projectile in a projectile weapon
	 */
	static class MeleeSpecs {
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
	}

	/**
	 * Defines a projectile trait that can possibly be applied to a projectile specs
	 */
	static class MeleeWeaponTrait {
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

	private final List<MeleeWeaponTrait> meleeTraits = Arrays.asList(
			// Slight base damage
			new MeleeWeaponTrait(2, 1.2f, null, (weapon, melee) -> {
				melee.baseDamage += 2;
			}),
			// Moderate base damage
			new MeleeWeaponTrait(10, 1.2f, null, (weapon, melee) -> {
				melee.baseDamage += 5;
			}),
			// Large base damage
			new MeleeWeaponTrait(25, 1.2f, null, (weapon, melee) -> {
				melee.baseDamage += 10;
			}),
			// Slight spread damage
			new MeleeWeaponTrait(1, 1.2f, null, (weapon, melee) -> {
				melee.spreadDamage += 1;
			}),
			// Moderate spread damage
			new MeleeWeaponTrait(5, 1.2f, null, (weapon, melee) -> {
				melee.spreadDamage += 3;
			}),
			// Large spread damage
			new MeleeWeaponTrait(12, 1.2f, null, (weapon, melee) -> {
				melee.spreadDamage += 5;
			}),
			// Fire type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.BURN;
				melee.statusChance = 0.2f;
			}),
			// Fire type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.BURN;
				melee.statusChance = 0.2f;
			}),
			// Poison type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.POISON;
				melee.statusChance = 0.2f;
			}),
			// Chill type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.CHILL;
				melee.statusChance = 0.2f;
			}),
			// Freeze type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.FROZEN;
				melee.statusChance = 0.2f;
			}),
			// Lightning type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.LIGHTNING;
				melee.statusChance = 0.2f;
			}),
			// Life steal type
			new MeleeWeaponTrait(15, 1.2f, "damageType", (weapon, melee) -> {
				melee.damageType = DamageType.ELEMENTAL;
				melee.statusEffect = StatusEffect.LIFE_STEAL;
				melee.statusChance = 0.2f;
			}),
			// Attack speed
			new MeleeWeaponTrait(10, 1.2f, "damageType", (weapon, melee) -> {
				weapon.cooldown *= 0.9f;
			}),
			// Stamina consumption
			new MeleeWeaponTrait(10, 1.2f, "damageType", (weapon, melee) -> {
				weapon.energyDrain *= 0.9f;
			})
	);

	private final List<String> swordSkins = Arrays.asList(
			"weapon_iron_dagger", "weapon_iron_rapier", "weapon_iron_shortsword", "weapon_iron_longsword", "weapon_iron_sawblade", "weapon_iron_broadsword",
			"weapon_bronze_dagger", "weapon_bronze_rapier", "weapon_bronze_shortsword", "weapon_bronze_longsword", "weapon_bronze_sawblade", "weapon_bronze_broadsword",
			"weapon_steel_dagger", "weapon_steel_rapier", "weapon_steel_shortsword", "weapon_steel_longsword", "weapon_steel_sawblade", "weapon_steel_broadsword"
	);

	private Weapon generateProjectileWeapon(int score) {
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

	private Weapon generateMeleeWeapon(int score) {
		WeaponSpecs weapon = new WeaponSpecs();
		weapon.cooldown = 0.5f;
		weapon.energyDrain = 10f;
		MeleeSpecs melee = new MeleeSpecs();
		int remaining = score;
		Set<String> unique = new HashSet<>();
		// Copy base traits
		LinkedList<MeleeWeaponTrait> traits = new LinkedList<>(meleeTraits);

		// Purchase as many traits as possible with the remaining score
		while (remaining > 0 && !traits.isEmpty()) {
			MeleeWeaponTrait trait = traits.remove(Rand.nextInt(traits.size()));
			// Cannot afford
			if (trait.cost > remaining) {
				continue;
			}
			// Has a unique label that has already been picked up
			if (trait.uniqueLabel != null && unique.contains(trait.uniqueLabel)) {
				continue;
			}

			trait.modifier.accept(weapon, melee);
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

		// Add attack (slash) sound
		modules.add(new SoundModule(Resources.sounds.get("audio/sound/slash.ogg")));

		// Create projectile prototype
		StringBuilder name = new StringBuilder();
		if (melee.statusEffect == StatusEffect.BURN) {
			name.append("Molten ");
		} else if (melee.statusEffect == StatusEffect.LIGHTNING) {
			name.append("Charged ");
		} else if (melee.statusEffect == StatusEffect.FROZEN) {
			name.append("Glacial ");
		} else if (melee.statusEffect == StatusEffect.CHILL) {
			name.append("Chilling ");
		} else if (melee.statusEffect == StatusEffect.POISON) {
			name.append("Venomous ");
		} else if (melee.statusEffect == StatusEffect.LIFE_STEAL) {
			name.append("Vampiric ");
		} else {
			name.append("Earthen ");
		}

		StringBuilder skin = new StringBuilder("weapon_");
		Color color;
		switch(Rand.nextInt(3)) {
			case 0:
				skin.append("iron_");
				color = Color.valueOf("B1C9C1");
				break;
			case 1:
				skin.append("bronze_");
				color = Color.valueOf("CD8032");
				break;
			default:
				skin.append("steel_");
				color = Color.valueOf("6BAFBD");
				break;
		}
		// TODO Make weapon types based on something more interesting (like how scatter damage compares to base damage, or range/speed)
		if (melee.baseDamage < 3) {
			skin.append("dagger");
			name.append("dagger");
		} else if (melee.baseDamage < 5) {
			skin.append("rapier");
			name.append("rapier");
		} else if (melee.baseDamage < 10) {
			skin.append("shortsword");
			name.append("short sword");
		} else if (melee.baseDamage < 15) {
			skin.append("longsword");
			name.append("long sword");
		} else if (melee.baseDamage < 20) {
			skin.append("sawblade");
			name.append("saw blade");
		} else {
			skin.append("broadsword");
			name.append("broadsword");
		}

		// TODO Use weapon's actual range in the hit box
		Vector2 hitBoundingBox = new Vector2(32, 32);
		EntityPrototype attack = new EntityPrototype(DungeonResources.prototypes.get("weapon_melee_attack"))
				.boundingBox(hitBoundingBox)
				.speed(0)
				.timeToLive(0.0001f)
				.hitPredicate(PlayerEntity.HIT_NON_PLAYERS);
		EntityPrototype hit = DungeonResources.prototypes.get("weapon_melee_hit");
		// TODO Will we reinstate the slash?
//		Animation<Material> slashAnimation = Resources.animations.get("melee_slash");
//		EntityPrototype slash = new EntityPrototype()
//				.animation(slashAnimation)
//				.boundingBox(hitBoundingBox)
//				.drawOffset(hitDrawOffset)
//				.timeToLive(slashAnimation.getAnimationDuration());
		modules.add(new AttackModule.Builder().prototype(attack).prototypeHit(hit).damageType(melee.damageType).minDamage(melee.baseDamage).maxDamage(melee.baseDamage + melee.spreadDamage).spawnDistance(16).hitCount(melee.hitCount).build());
		return new ModularWeapon(name.toString(), Resources.animations.get(skin.toString()), modules, weapon.cooldown, weapon.energyDrain, (int) weapon.goldCost, color);
	}

	public Weapon generate(int score) {
		if (Rand.chance(0.5f)) {
		return generateProjectileWeapon(score);
		} else {
			return generateMeleeWeapon(score);
		}
	}


}
