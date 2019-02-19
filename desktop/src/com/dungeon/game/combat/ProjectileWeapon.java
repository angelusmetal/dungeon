package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.Projectile;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ProjectileWeapon extends Weapon {

	public ProjectileWeapon(String name, Supplier<Float> damage, DamageType damageType, float knockback) {
		super(name, damage, damageType, knockback);
	}

	protected void createProjectile(EntityPrototype prototype, Function<Entity, Attack> attackSupplier, Vector2 origin, Vector2 aim) {
		Projectile projectile = new Projectile(origin, prototype, attackSupplier);
		impulse(projectile, aim);
		Engine.entities.add(projectile);
	}

	protected void createProjectileFan(EntityPrototype prototype, Function<Entity, Attack> attackSupplier, Vector2 origin, Vector2 aim, int count, int spread) {
		Vector2 aim2 = aim.cpy().rotate((count - 1) * spread / -2f);
		for (int i = 0; i < count; ++i) {
			Projectile projectile = new Projectile(origin, prototype, attackSupplier);
			impulse(projectile, aim2);
			Engine.entities.add(projectile);
			aim2.rotate(spread);
		}
	}

}
