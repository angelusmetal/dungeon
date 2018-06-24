package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.state.GameState;

import java.util.function.Supplier;

public abstract class ProjectileWeapon extends Weapon {

//	private final EntityPrototype projectile;
//	private final EntityPrototype impact;
//	private final EntityPrototype trail;

	public ProjectileWeapon(Supplier<Float> damage, DamageType damageType, float knockback) {
		super(damage, damageType, knockback);
//		projectile = getProjectile();
//		impact = getImpact();
//		trail = getTrail();
	}

	protected abstract float getSpawnDistance();
//	protected abstract EntityPrototype getProjectile();
//	protected abstract EntityPrototype getImpact();
//	protected abstract EntityPrototype getTrail();

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = shift(origin, aim, getSpawnDistance());
		Entity entity = createProjectile(hitOrigin, aim);
		impulse(entity, aim);
		GameState.addEntity(entity);
	}

	protected abstract Entity createProjectile(Vector2 origin, Vector2 aim);

}
