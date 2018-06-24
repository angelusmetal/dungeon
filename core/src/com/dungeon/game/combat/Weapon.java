package com.dungeon.game.combat;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Projectile;

import java.util.function.Supplier;

public abstract class Weapon {
	private Supplier<Float> damage;
	private DamageType damageType;
	private float knockback;

	public Weapon(Supplier<Float> damage, DamageType damageType, float knockback) {
		this.damage = damage;
		this.damageType = damageType;
		this.knockback = knockback;
	}

	/**
	 * Create the required entities to spawn an attack in the world
	 */
	public abstract void spawnEntities(Vector2 origin, Vector2 aim);

	/**
	 * @return A newly created {@link Attack} instance
	 */
	public Attack createAttack() {
		return new Attack(damage.get(), damageType, knockback);
	}

//	protected void sers() {
//			// TODO Take separation from attack
//			Entity projectile = createProjectile(getPos().cpy().mulAdd(getAim(), 20));
//			if (projectile != null) {
//				projectile.impulse(getAim().cpy().setLength(projectile.speed));
//				// Extra offset to make projectiles appear in the character's hands
//				//projectile.getPos().y -= 8;
//				GameState.addEntity(projectile);
//			}
//	}

	protected Vector2 shift(Vector2 origin, Vector2 direction, float distance) {
		return origin.cpy().mulAdd(direction, distance);
	}

	protected void impulse(Entity projectile, Vector2 direction) {
		projectile.impulse(direction.cpy().setLength(projectile.getSpeed()));
	}
}
