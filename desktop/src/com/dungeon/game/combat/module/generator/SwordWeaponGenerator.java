package com.dungeon.game.combat.module.generator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
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

import static com.dungeon.engine.util.Util.clamp;

public class SwordWeaponGenerator {

	private enum SwordType {
		DAGGER, SHORT_SWORD, LONG_SWORD, RAPIER, SAW_BLADE, BROAD_SWORD
	}

	private enum SwordMaterial {
		IRON, BRONZE, STEEL
	}

	private final Color VENOM_COLOR = Color.valueOf("51FF00");
	private final Color CHILL_COLOR = Color.valueOf("00A2FF");
	private final Color FREEZE_COLOR = Color.valueOf("0062FF");

	/** Elemental weapons will only happen above a certain score */
	private final int ELEMENTAL_MIN_SCORE = 8;
	/** Elemental weapons will only happen a fraction of times */
	private final float ELEMENTAL_CHANCE = 0.2f;
	/** Elemental damage has lower base damage (but applies an effect) */
	private final float ELEMENTAL_DAMAGE_RATIO = 0.75f;

	private static class SwordSpec {
		float goldCost = 50f;
		float cooldown = 0.5f;
		float energyDrain = 10f;
		float statusChance;
		StatusEffect statusEffect;
		float statusDamage;
		float statusDuration;
		/** Minimum damage */
		float baseDamage = 1;
		/** How much the damage can spread above base damage */
		float spreadDamage = 0;
		/** Damage type */
		DamageType damageType;
		/** How many things a single attack can hit */
		int hitCount = 1;
		/** how many units (pixel) does this weapon hit */
		int range;
		Consumer<DungeonEntity> statusAction = e -> {};
		Color color;
		SwordMaterial material;
		SwordType type;
	}

	public Weapon generate(int level) {
		SwordSpec weapon = new SwordSpec();

		// Quality is a random number between 0 (worst) and 1 (optimal)
		float quality = Rand.between(0f, 1f);
		// But level will increase quality (the higher the level, the closer to 1, but with diminishing returns as level increases)
		quality = clamp(quality + 1f - (float) (Math.pow(level, -0.5)));

		// Magic equation to get a nice damage ramp up
		weapon.baseDamage = level * level * 0.8f + 3;

		// Build a weapon based off the traits
		List<WeaponModule> modules = new ArrayList<>();
		StringBuilder name = new StringBuilder();
		StringBuilder skin = new StringBuilder("weapon_");

		// Add attack (slash) sound
		modules.add(new SoundModule(Resources.sounds.get("audio/sound/slash.ogg")));

		boolean isElemental = level >= ELEMENTAL_MIN_SCORE && Rand.chance(ELEMENTAL_CHANCE);
//		isElemental = true;
		if (isElemental) {
			setElemental(weapon, quality, name);
		}

		// Pick a random material and update weapon
		weapon.material = Rand.pick(SwordMaterial.class);
		setMaterial(weapon, name, skin);

		// Pick a random type and update weapon
		weapon.type = Rand.pick(SwordType.class);
		setType(weapon, name, skin);

		weapon.goldCost = (float) Math.pow(level, 1.5d) * 1.2f * weapon.goldCost;

		// TODO Use weapon's actual range in the hit box
		Vector2 hitBoundingBox = new Vector2(weapon.range, weapon.range);
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
				.damageType(weapon.damageType)
				.minDamage(weapon.baseDamage)
				.maxDamage(weapon.baseDamage + weapon.spreadDamage)
				.spawnDistance(weapon.range / 2f)
				.hitCount(weapon.hitCount);
		if (weapon.statusEffect != null) {
			builder.status(weapon.statusChance, weapon.statusAction);
		}
		modules.add(builder.build());
		return new ModularWeapon(name.toString(), Resources.animations.get(skin.toString()), modules, weapon.cooldown, weapon.energyDrain, (int) weapon.goldCost, weapon.color);
	}

	private void setType(SwordSpec weapon, StringBuilder name, StringBuilder skin) {
		switch(weapon.type) {
			case DAGGER:
				skin.append("dagger");
				name.append("Dagger");
				weapon.hitCount = 1;
				weapon.baseDamage *= 0.8f;
				weapon.spreadDamage = 0;
				weapon.range = 20;
				weapon.cooldown *= 0.8f;
				weapon.energyDrain *= 0.8f;
				weapon.goldCost = 50f;
				break;
			case SHORT_SWORD:
				skin.append("shortsword");
				name.append("Short Sword");
				weapon.hitCount = 1;
				weapon.spreadDamage = weapon.baseDamage * 0.2f;
				weapon.baseDamage *= 0.9f;
				weapon.range = 25;
				weapon.cooldown *= 1.1f;
				weapon.energyDrain *= 1.1f;
				break;
			case RAPIER:
				skin.append("rapier");
				name.append("Rapier");
				weapon.hitCount = 1;
				weapon.spreadDamage = weapon.baseDamage * 0.1f;
//				weapon.baseDamage *= 0.9f;
				weapon.range = 27;
//				weapon.cooldown *= 1.0f;
//				weapon.energyDrain *= 1.0f;
				break;
			case LONG_SWORD:
				skin.append("longsword");
				name.append("Long Sword");
				weapon.hitCount = 1000;
				weapon.spreadDamage = weapon.baseDamage * 0.3f;
				weapon.baseDamage *= 0.8f;
				weapon.range = 32;
				weapon.cooldown *= 1.2f;
				weapon.energyDrain *= 1.2f;
				break;
			case SAW_BLADE:
				skin.append("sawblade");
				name.append("Saw Blade");
				weapon.hitCount = 1000;
				weapon.spreadDamage = weapon.baseDamage * 0.7f;
//				weapon.baseDamage *= 0.6f;
				weapon.range = 40;
				weapon.cooldown *= 1.3f;
				weapon.energyDrain *= 1.9f;
				break;
			case BROAD_SWORD:
				skin.append("broadsword");
				name.append("Broadsword");
				weapon.hitCount = 1000;
				weapon.spreadDamage = weapon.baseDamage * 0.4f;
				weapon.baseDamage *= 0.9f;
				weapon.range = 36;
				weapon.cooldown *= 1.3f;
				weapon.energyDrain *= 1.5f;
				break;
		}
	}

	private void setMaterial(SwordSpec weapon, StringBuilder name, StringBuilder skin) {
		switch(weapon.material) {
			case IRON:
				name.append("Iron ");
				skin.append("iron_");
				weapon.color = Color.valueOf("B1C9C1");
				weapon.baseDamage *= 1.1f;
				weapon.energyDrain *= 1.1f;
				weapon.goldCost = 50f;
				break;
			case BRONZE:
				name.append("Bronze ");
				skin.append("bronze_");
				weapon.color = Color.valueOf("CD8032");
				weapon.goldCost = 60f;
				break;
			default://case STEEL:
				name.append("Steel ");
				skin.append("steel_");
				weapon.color = Color.valueOf("6BAFBD");
				weapon.baseDamage *= 0.9f;
				weapon.energyDrain *= 0.9f;
				weapon.goldCost = 70f;
				break;
		}
	}

	private void setElemental(SwordSpec weapon, float quality, StringBuilder name) {
		// Make weapon elemental
		weapon.damageType = DamageType.ELEMENTAL;
		weapon.statusEffect = Rand.pick(StatusEffect.class);
		weapon.baseDamage *= ELEMENTAL_DAMAGE_RATIO;

		// Status chance will start at 0.1 and will increase with quality until a max of around 0.4
		weapon.statusChance = (float) (Math.pow(quality/2f, 2) + 0.1);
		weapon.statusDamage = weapon.baseDamage * weapon.statusChance;
		weapon.statusDuration = 30f * weapon.statusChance;

//		weapon.statusEffect = StatusEffect.POISON;
		if (weapon.statusEffect == StatusEffect.BURN) {
			name.append("Molten ");
			Attack statusAttack = new Attack(null, weapon.statusDamage, DamageType.ELEMENTAL, 0);
			EntityPrototype flame = DungeonResources.prototypes.get("particle_burn");
			Function<Entity, Entity> particleProvider = e -> {
				DungeonEntity particle = new DungeonEntity(flame, e.getOrigin());
				particle.getOrigin().add(Rand.between(e.getBody().getBoundingBox().x / -2f, e.getBody().getBoundingBox().x / 2f), 0f);
				particle.setZPos(Rand.between(16, 32));
				return particle;
			};
			weapon.statusAction = entity -> entity.addTrait(DungeonTraits.damageEffect(statusAttack, 1f, weapon.statusDuration, 0.2f, particleProvider).get(entity));
		} else if (weapon.statusEffect == StatusEffect.LIGHTNING) {
			name.append("Charged ");
		} else if (weapon.statusEffect == StatusEffect.FROZEN) {
			name.append("Glacial ");
		} else if (weapon.statusEffect == StatusEffect.CHILL) {
			name.append("Chilling ");
			// TODO Slow enemy
		} else if (weapon.statusEffect == StatusEffect.POISON) {
			name.append("Venomous ");
			// Does much less damage, but for quite longer
			weapon.statusDamage /= 10f;
			weapon.statusDuration *= 4f;
			Attack statusAttack = new Attack(null, weapon.statusDamage, DamageType.ELEMENTAL, 0);
			EntityPrototype poison = DungeonResources.prototypes.get("particle_poison");
			Function<Entity, Entity> particleProvider = e -> {
				DungeonEntity particle = new DungeonEntity(poison, e.getOrigin());
				particle.getOrigin().add(Rand.between(e.getBody().getBoundingBox().x / -2f, e.getBody().getBoundingBox().x / 2f), 0f);
				particle.setZPos(Rand.between(16, 32));
				return particle;
			};
			weapon.statusAction = entity -> {
				entity.addTrait(DungeonTraits.damageEffect(statusAttack, 1f, weapon.statusDuration, 0.2f, particleProvider).get(entity));
				entity.addTrait(Traits.colorize(VENOM_COLOR, weapon.statusDuration).get(entity));
			};
		} else if (weapon.statusEffect == StatusEffect.LIFE_STEAL) {
			name.append("Vampiric ");
		}
	}
}
