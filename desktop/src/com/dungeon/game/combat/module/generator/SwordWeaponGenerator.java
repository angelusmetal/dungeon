package com.dungeon.game.combat.module.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.StatusEffect;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.combat.module.AttackModule;
import com.dungeon.game.combat.module.ModularWeapon;
import com.dungeon.game.combat.module.SoundModule;
import com.dungeon.game.combat.module.WeaponModule;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.DungeonTraits;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.DungeonResources;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SwordWeaponGenerator {

	private enum SwordType {
		DAGGER, SHORT_SWORD, LONG_SWORD, RAPIER, SAW_BLADE, BROAD_SWORD
	}

	private enum SwordMaterial {
		IRON, BRONZE, STEEL
	}

	/** Elemental weapons will only happen above a certain score */
	private final int ELEMENTAL_MIN_SCORE = 8;
	/** Elemental weapons will only happen a fraction of times */
	private final float ELEMENTAL_CHANCE = 0.2f;
	/** Elemental damage has lower base damage (but applies an effect) */
	private final float ELEMENTAL_DAMAGE_RATIO = 0.75f;

	public Weapon generate(int score) {
		WeaponSpecs weapon = new WeaponSpecs();
		weapon.cooldown = 0.5f;
		weapon.energyDrain = 10f;
		MeleeSpecs melee = new MeleeSpecs();

		// Magic equation to get a nice damage ramp up
		melee.baseDamage = score * score * 0.8f + 3;

		// Build a weapon based off the traits
		List<WeaponModule> modules = new ArrayList<>();

		if (score >= ELEMENTAL_MIN_SCORE && Rand.chance(ELEMENTAL_CHANCE)) {
			// Make weapon elemental
			melee.damageType = DamageType.ELEMENTAL;
			melee.statusEffect = Rand.pick(StatusEffect.class);
			melee.baseDamage *= ELEMENTAL_DAMAGE_RATIO;
		}

		// Add attack (slash) sound
		modules.add(new SoundModule(Resources.sounds.get("audio/sound/slash.ogg")));

		// Create projectile prototype
		StringBuilder name = new StringBuilder();
		Consumer<DungeonEntity> statusAction = e -> {};
		if (melee.statusEffect == StatusEffect.BURN) {
			name.append("Molten ");
			Attack statusAttack = new Attack(null, 10, DamageType.ELEMENTAL, 0);
			EntityPrototype flame = DungeonResources.prototypes.get("particle_flame");
			Function<Entity, Entity> particleProvider = e -> new DungeonEntity(flame, e.getOrigin());
			melee.statusChance = 0.2f;
			statusAction = entity -> {
				entity.addTrait(DungeonTraits.damageEffect(statusAttack, 1f, 5f, 0.2f, particleProvider).get(entity));
			};
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
				melee.baseDamage *= 1.1f;
				weapon.energyDrain *= 1.1f;
				weapon.goldCost = 50f;
				break;
			case BRONZE:
				name.append("Bronze ");
				skin.append("bronze_");
				color = Color.valueOf("CD8032");
				weapon.goldCost = 60f;
				break;
			default://case STEEL:
				name.append("Steel ");
				skin.append("steel_");
				color = Color.valueOf("6BAFBD");
				melee.baseDamage *= 0.9f;
				weapon.energyDrain *= 0.9f;
				weapon.goldCost = 70f;
				break;
		}
		// TODO Make weapon types based on something more interesting (like how scatter damage compares to base damage, or range/speed)
		switch(Rand.pick(SwordType.class)) {
			case DAGGER:
				skin.append("dagger");
				name.append("Dagger");
				melee.hitCount = 1;
				melee.baseDamage *= 0.8f;
				melee.spreadDamage = 0;
				melee.range = 20;
				weapon.cooldown *= 0.8f;
				weapon.energyDrain *= 0.8f;
				weapon.goldCost = 50f;
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
//				melee.baseDamage *= 0.6f;
				melee.range = 40;
				weapon.cooldown *= 1.3f;
				weapon.energyDrain *= 1.9f;
				break;
			case BROAD_SWORD:
				skin.append("broadsword");
				name.append("Broadsword");
				melee.hitCount = 1000;
				melee.spreadDamage = melee.baseDamage * 0.4f;
				melee.baseDamage *= 0.9f;
				melee.range = 36;
				weapon.cooldown *= 1.3f;
				weapon.energyDrain *= 1.5f;
				break;
		}

		weapon.goldCost = (float) Math.pow(score, 1.5d) * 1.2f * weapon.goldCost;

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
		AttackModule.Builder builder = new AttackModule.Builder()
				.prototype(attack)
				.prototypeHit(hit)
				.damageType(melee.damageType)
				.minDamage(melee.baseDamage)
				.maxDamage(melee.baseDamage + melee.spreadDamage)
				.spawnDistance(melee.range / 2f)
				.hitCount(melee.hitCount);
		if (melee.statusEffect != null) {
			builder.status(melee.statusChance, statusAction);
		}
		modules.add(builder.build());
		return new ModularWeapon(name.toString(), Resources.animations.get(skin.toString()), modules, weapon.cooldown, weapon.energyDrain, (int) weapon.goldCost, color);
	}
}
