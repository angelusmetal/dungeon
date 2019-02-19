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
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		createProjectile(projectile, this::createAttack, origin, aim);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 5f);
	}

}
