package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.entity.Projectile;
import com.dungeon.game.resource.Resources;

import java.util.function.Supplier;

public class CatStaffWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;
	private final int tier = (int) Game.getDifficultyTier();

	public CatStaffWeapon() {
		super("Cat staff", damageSupplier(), DamageType.ELEMENTAL, 0);
		projectile = Resources.prototypes.get("cat_bullet")
				.with(Traits.autoSeek(0.1f, 60, () -> Engine.entities.dynamic().filter(PlayerEntity.TARGET_NON_PLAYER_CHARACTERS)));
	}

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		createProjectileFan(projectile, this::createAttack, origin, aim, tier, 10);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 3f);
	}
}
