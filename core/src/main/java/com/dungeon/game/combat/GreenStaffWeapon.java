package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;

import java.util.function.Supplier;

public class GreenStaffWeapon extends ProjectileWeapon {

	private final EntityPrototype projectile;
	private final EntityPrototype impact;

	public GreenStaffWeapon() {
		super("Green staff", damageSupplier(), DamageType.ELEMENTAL, 0);

		projectile = Resources.prototypes.get("green_staff_projectile");
		impact = Resources.prototypes.get("green_staff_projectile_impact");
	}

	@Override
	protected float getSpawnDistance() {
		return 2;
	}

	@Override
	protected Entity createProjectile(Vector2 origin, Vector2 aim) {
		return new Projectile(origin, projectile, this::createAttack) {
			@Override
			protected void onExpire() {
				GameState.entities.add(createImpact(getOrigin()));
			}
		};
	}

	private Entity createImpact(Vector2 origin) {
		return new Entity(impact, origin);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = GameState.getDifficultyTier();
		return () -> tier * Rand.between(2f, 5f);
	}
}
