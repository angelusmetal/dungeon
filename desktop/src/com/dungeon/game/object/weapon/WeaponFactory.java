package com.dungeon.game.object.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.combat.module.AimedParticleModule;
import com.dungeon.game.combat.module.AttackModule;
import com.dungeon.game.combat.module.ArcAttackModule;
import com.dungeon.game.combat.module.ModularWeapon;
import com.dungeon.game.combat.module.ModularWeaponGenerator;
import com.dungeon.game.combat.module.SoundModule;
import com.dungeon.game.combat.module.WeaponModule;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.DungeonResources;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class WeaponFactory {

	ModularWeaponGenerator generator = new ModularWeaponGenerator();

	public Entity sword(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, () -> buildSword(Game.getDifficultyTier()));
	}

	public Entity catStaff(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, () -> buildCatStaff(Game.getDifficultyTier()));
	}

	public Entity greenStaff(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, () -> buildVenomStaff(Game.getDifficultyTier()));
	}

	public Entity devastatorStaff(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, () -> buildDevastatorStaff(Game.getDifficultyTier()));
	}

	public Entity random(Vector2 origin, EntityPrototype prototype) {
		return buildWeaponEntity(origin, prototype, () -> buildRandom(Game.getDifficultyTier()));
	}

	public Weapon buildSword(float tier) {
		Animation<TextureRegion> slashAnimation = Resources.animations.get("melee_slash");
		TextureRegion referenceFrame = slashAnimation.getKeyFrame(0);
		Vector2 hitDrawOffset = new Vector2(referenceFrame.getRegionWidth() / 2f, referenceFrame.getRegionHeight() / 2f);
		Vector2 hitBoundingBox = new Vector2(32, 32);
		EntityPrototype hit = new EntityPrototype()
				.animation(Resources.animations.get("invisible"))
				.boundingBox(hitBoundingBox)
				.drawOffset(hitDrawOffset)
				.speed(1)
				.timeToLive(slashAnimation.getAnimationDuration())
				.hitPredicate(PlayerEntity.HIT_NON_PLAYERS);
		EntityPrototype slash = new EntityPrototype()
				.animation(slashAnimation)
				.boundingBox(hitBoundingBox)
				.drawOffset(hitDrawOffset)
				.timeToLive(slashAnimation.getAnimationDuration());
		float minDps = tier * 2f;
		float maxDps = tier * 5f;
		List<WeaponModule> modules = Arrays.asList(
				new AttackModule(hit, DamageType.NORMAL, minDps, maxDps).spawnDistance(16),
				new AimedParticleModule(slash).spawnDistance(4),
				new SoundModule(Resources.sounds.get("audio/sound/slash.ogg"))
		);
		return new ModularWeapon("Sword", Resources.animations.get("weapon_iron_shortsword"), modules, 0.35f, 15, 75);
	}

	public Weapon buildCatStaff(float tier) {
		EntityPrototype projectile = new EntityPrototype(DungeonResources.prototypes.get("projectile_cat"))
				.with(Traits.autoSeek(0.1f, 60, () -> Engine.entities.dynamic().filter(PlayerEntity.TARGET_NON_PLAYER_CHARACTERS)));
		float minDps = tier * 2f;
		float maxDps = tier * 3f;
		List<WeaponModule> modules = Arrays.asList(
				new AttackModule(projectile, DamageType.ELEMENTAL, minDps, maxDps),
				new SoundModule(Resources.sounds.get("audio/sound/magic_bolt.ogg"))
		);
		return new ModularWeapon("Cat staff", Resources.animations.get("weapon_fire_wand"), modules, 0.35f, 15, 80);
	}

	public Weapon buildVenomStaff(float tier) {
		EntityPrototype projectile = DungeonResources.prototypes.get("venom_bullet");
		EntityPrototype projectileInv = DungeonResources.prototypes.get("venom_bullet_inverse");
		float minDps = tier * 1f;
		float maxDps = tier * 2f;
		List<WeaponModule> modules = Arrays.asList(
				new AttackModule(projectile, DamageType.ELEMENTAL, minDps, maxDps),
				new AttackModule(projectileInv, DamageType.ELEMENTAL, minDps, maxDps),
				new SoundModule(Resources.sounds.get("audio/sound/magic_bolt.ogg"))
		);
		return new ModularWeapon("Venom staff", Resources.animations.get("weapon_poison_wand"), modules, 0.25f, 20, 75);
	}

	public Weapon buildDevastatorStaff(float tier) {
		EntityPrototype projectile = new EntityPrototype(DungeonResources.prototypes.get("devastator_bullet"))
				.with(Traits.autoSeek(0.1f, 60, () -> Engine.entities.dynamic().filter(PlayerEntity.TARGET_NON_PLAYER_CHARACTERS)));
		float minDps = tier * 200f;
		float maxDps = tier * 300f;
		List<WeaponModule> modules = Arrays.asList(
				new ArcAttackModule(projectile, DamageType.ELEMENTAL, minDps, maxDps, (int) Game.getDifficultyTier() + 5, 10),
				new SoundModule(Resources.sounds.get("audio/sound/magic_bolt.ogg"))
		);
		return new ModularWeapon("Devastator staff", Resources.animations.get("weapon_poison_scepter"), modules, 0.35f, 15, 10000);
	}

	public Weapon buildFireballStaff(float tier) {
		EntityPrototype projectile = new EntityPrototype(DungeonResources.prototypes.get("slime_fireball"));
		float minDps = tier * 2f;
		float maxDps = tier * 5f;
		List<WeaponModule> modules = Arrays.asList(
				new AttackModule(projectile, DamageType.ELEMENTAL, minDps, maxDps),
				new SoundModule(Resources.sounds.get("audio/sound/firebolt.ogg"))
		);
		return new ModularWeapon("Fireball", Resources.animations.get("weapon_fire_staff"), modules, 0.25f, 20, 75);
	}

	public Weapon buildRandom(float tier) {
		return generator.generate(Rand.between((int) (tier * 10), (int) (tier * 13f)));
	}

	public Entity buildWeaponEntity(Vector2 origin, EntityPrototype prototype, Supplier<Weapon> weaponSupplier) {
		Weapon weapon = weaponSupplier.get();
		DungeonEntity weaponEntity = new DungeonEntity(prototype, origin) {
			@Override
			protected boolean onEntityCollision(Entity other) {
				if (!expired && other instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) other;
//					weapon.setAnimation(getAnimation());
					character.getPlayer().setWeapon(weapon);
					character.getPlayer().getConsole().log("Picked up " + weapon.getName() + "!", Color.GOLD);
					expire();
					return true;
				}
				return false;
			}
		};
		weaponEntity.setAnimation(weapon.getAnimation(), Engine.time());
		return weaponEntity;
	}

}
