package com.dungeon.game.combat;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.entity.Projectile;
import com.dungeon.game.resource.Resources;

import java.util.function.Supplier;

public class VenomStaffWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;
	private final EntityPrototype projectileInv;
	private final float tier = Game.getDifficultyTier();
	Sound soundAttack = Resources.sounds.get("audio/sound/magic_bolt.ogg");

	public VenomStaffWeapon() {
		super("Venom staff", damageSupplier(), DamageType.ELEMENTAL, 0);
		projectile = Resources.prototypes.get("venom_bullet");
		projectileInv = Resources.prototypes.get("venom_bullet_inverse");
	}

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		createProjectile(projectile, this::createAttack, origin, aim);
		if (tier > 2) {
			createProjectile(projectileInv, this::createAttack, origin, aim);
		}
		Engine.audio.playSound(soundAttack, origin, 1f, 0.05f);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		if (tier > 2) {
			return () -> Game.getDifficultyTier() * Rand.between(1f, 3f);
		} else {
			return () -> Game.getDifficultyTier() * Rand.between(2f, 5f);
		}
	}
}
