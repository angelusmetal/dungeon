package com.dungeon.game.combat.module.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.StatusEffect;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.combat.module.AttackModule;
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

public class SwordWeaponGenerator {

	private enum SwordType {
		DAGGER, SHORT_SWORD, LONG_SWORD, RAPIER, SAW_BLADE, BROAD_SWORD
	}

	private enum SwordMaterial {
		IRON, BRONZE, STEEL
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
			})
	);

	public Weapon generate(int score) {
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
//		} else {
//			name.append("Earthen ");
		}

		StringBuilder skin = new StringBuilder("weapon_");
		Color color;
		switch(Rand.pick(SwordMaterial.class)) {
			case IRON:
				name.append("Iron ");
				skin.append("iron_");
				color = Color.valueOf("B1C9C1");
				break;
			case BRONZE:
				name.append("Bronze ");
				skin.append("bronze_");
				color = Color.valueOf("CD8032");
				break;
			default://case STEEL:
				name.append("Steel ");
				skin.append("steel_");
				color = Color.valueOf("6BAFBD");
				break;
		}
		// TODO Make weapon types based on something more interesting (like how scatter damage compares to base damage, or range/speed)
		switch(Rand.pick(SwordType.class)) {
			case DAGGER:
				skin.append("dagger");
				name.append("Dagger");
				melee.hitCount = 1;
				melee.baseDamage = 0.8f;
				melee.spreadDamage = 0;
				melee.range = 20;
				weapon.cooldown *= 0.8f;
				weapon.energyDrain *= 0.8f;
				break;
			case SHORT_SWORD:
				skin.append("shortsword");
				name.append("Short Sword");
				melee.hitCount = 1;
				melee.spreadDamage = melee.baseDamage * 0.2f;
				melee.baseDamage *= 0.9f;
				melee.range = 25;
				weapon.cooldown *= 1.1f;
				weapon.energyDrain *= 1.1f;
				break;
			case RAPIER:
				skin.append("rapier");
				name.append("Rapier");
				melee.hitCount = 1;
				melee.spreadDamage = melee.baseDamage * 0.1f;
//				melee.baseDamage *= 0.9f;
				melee.range = 27;
//				weapon.cooldown *= 1.0f;
//				weapon.energyDrain *= 1.0f;
				break;
			case LONG_SWORD:
				skin.append("longsword");
				name.append("Long Sword");
				melee.hitCount = 1000;
				melee.spreadDamage = melee.baseDamage * 0.3f;
				melee.baseDamage *= 0.8f;
				melee.range = 32;
				weapon.cooldown *= 1.2f;
				weapon.energyDrain *= 1.2f;
				break;
			case SAW_BLADE:
				skin.append("sawblade");
				name.append("Saw Blade");
				melee.hitCount = 1000;
				melee.spreadDamage = melee.baseDamage * 0.7f;
				melee.baseDamage *= 0.6f;
				melee.range = 40;
				weapon.cooldown *= 1.4f;
				weapon.energyDrain *= 1.9f;
				break;
			case BROAD_SWORD:
				skin.append("broadsword");
				name.append("Broadsword");
				melee.hitCount = 1000;
				melee.spreadDamage = melee.baseDamage * 0.2f;
				melee.baseDamage *= 0.9f;
				melee.range = 36;
				weapon.cooldown *= 1.3f;
				weapon.energyDrain *= 1.5f;
				break;
		}

		// TODO Use weapon's actual range in the hit box
		Vector2 hitBoundingBox = new Vector2(melee.range, melee.range);
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
		modules.add(new AttackModule.Builder().prototype(attack).prototypeHit(hit).damageType(melee.damageType).minDamage(melee.baseDamage).maxDamage(melee.baseDamage + melee.spreadDamage).spawnDistance(melee.range / 2f).hitCount(melee.hitCount).build());
		return new ModularWeapon(name.toString(), Resources.animations.get(skin.toString()), modules, weapon.cooldown, weapon.energyDrain, (int) weapon.goldCost, color);
	}
}
