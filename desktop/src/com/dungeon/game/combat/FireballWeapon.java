package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.entity.Projectile;
import com.dungeon.game.resource.Resources;

import java.util.function.Supplier;

public class FireballWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;

	public FireballWeapon() {
		super("Fireball", damageSupplier(), DamageType.ELEMENTAL, 0);

		projectile = Resources.prototypes.get("slime_fireball");
	}

	@Override
	protected float getSpawnDistance() {
		return 2;
	}

	@Override
	protected Entity createProjectile(Vector2 origin, Vector2 aim) {
		return new Projectile(origin, projectile, this::createAttack) {};
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 5f);
	}
}
