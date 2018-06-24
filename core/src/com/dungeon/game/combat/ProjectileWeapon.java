package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.state.GameState;

import java.util.function.Supplier;

public abstract class ProjectileWeapon extends Weapon {

	public ProjectileWeapon(String name, Supplier<Float> damage, DamageType damageType, float knockback) {
		super(name, damage, damageType, knockback);
	}

	protected abstract float getSpawnDistance();

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = shift(origin, aim, getSpawnDistance());
		Entity entity = createProjectile(hitOrigin, aim);
		impulse(entity, aim);
		GameState.addEntity(entity);
	}

	protected abstract Entity createProjectile(Vector2 origin, Vector2 aim);

}
