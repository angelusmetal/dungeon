package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.resource.Resources;

import java.util.function.Supplier;

public class FireboltWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;
	private final int tier = (int) Game.getDifficultyTier();

	public FireboltWeapon() {
		super("Firebolt", damageSupplier(), DamageType.ELEMENTAL, 0);
		projectile = new EntityPrototype(Resources.prototypes.get("firebolt"));

	}

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		createProjectileFan(projectile, this::createAttack, origin, aim, tier, 10);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 5f);
	}

}
