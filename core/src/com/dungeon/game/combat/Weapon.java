package com.dungeon.game.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.resource.TextureResource;

import java.util.function.Supplier;

public abstract class Weapon {
	private String name;
	private Supplier<Float> damage;
	private DamageType damageType;
	private float knockback;
	private Animation<TextureRegion> animation;

	public Weapon(String name, Supplier<Float> damage, DamageType damageType, float knockback) {
		this.name = name;
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

	protected Vector2 shift(Vector2 origin, Vector2 direction, float distance) {
		return origin.cpy().mulAdd(direction, distance);
	}

	protected void impulse(Entity projectile, Vector2 direction) {
		projectile.impulse(direction.cpy().setLength(projectile.getSpeed()));
	}

	public String getName() {
		return name;
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	public void setAnimation(Animation<TextureRegion> animation) {
		this.animation = animation;
	}
}
