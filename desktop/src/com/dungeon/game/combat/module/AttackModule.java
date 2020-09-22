package com.dungeon.game.combat.module;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.Projectile;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Creates an attack projectile
 */
public class AttackModule implements WeaponModule {
	final EntityPrototype prototype;
	final EntityPrototype prototypeHit;
	final DamageType damageType;
	final float minDamage;
	final float maxDamage;
	final Function<Entity, Attack> attackFunction;
	final float spawnDistance;
	final float statusChance;
	final Consumer<DungeonEntity> statusAction;
	int hitCount;

	private AttackModule(Builder builder) {
		prototype = builder.prototype;
		prototypeHit = builder.prototypeHit;
		damageType = builder.damageType;
		minDamage = builder.minDamage;
		maxDamage = builder.maxDamage;
		attackFunction = emitter -> new Attack(emitter, (int) (Rand.between(builder.minDamage, builder.maxDamage) + 0.5f), builder.damageType, builder.knockback);
		spawnDistance = builder.spawnDistance;
		hitCount = builder.hitCount;
		statusChance = builder.statusChance;
		statusAction = builder.statusAction;
	}

	@Override
	public void apply(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = origin.cpy().mulAdd(aim, spawnDistance);
		Projectile projectile;
		projectile = new Projectile(hitOrigin, prototype, attackFunction) {
			@Override
			protected boolean onEntityCollision(DungeonEntity entity) {
				boolean hit = super.onEntityCollision(entity);
				if (hit) {
					// If there is a prototype for hits, spawn an entity
					if (prototypeHit != null) {
						Vector2 hitOrigin = new Vector2(
								Util.clamp((entity.getOrigin().x + origin.x) / 2f, entity.getBody().getBottomLeft().x, entity.getBody().getTopRight().x),
								Util.clamp((entity.getOrigin().y + origin.y) / 2f, entity.getBody().getBottomLeft().y, entity.getBody().getTopRight().y));
						Engine.entities.add(new Entity(prototypeHit, hitOrigin));
					}
					if (Rand.chance(statusChance)) {
						statusAction.accept(entity);
					}
				}
				return hit;
			}
		};
		projectile.impulse(aim.cpy().setLength(projectile.getSpeed()));
		projectile.setHitCount(hitCount);
		projectile.spawn();
		Engine.entities.add(projectile);
	}

	public static final class Builder {
		private EntityPrototype prototype;
		private EntityPrototype prototypeHit;
		private DamageType damageType;
		private float minDamage;
		private float maxDamage;
		private float spawnDistance;
		private float knockback = 0;
		private int hitCount = 1;
		private float statusChance;
		private Consumer<DungeonEntity> statusAction;

		public Builder() {
		}

		public Builder prototype(EntityPrototype prototype) {
			this.prototype = prototype;
			return this;
		}

		public Builder prototypeHit(EntityPrototype prototypeHit) {
			this.prototypeHit = prototypeHit;
			return this;
		}

		public Builder damageType(DamageType damageType) {
			this.damageType = damageType;
			return this;
		}

		public Builder minDamage(float minDamage) {
			this.minDamage = minDamage;
			return this;
		}

		public Builder maxDamage(float maxDamage) {
			this.maxDamage = maxDamage;
			return this;
		}

		public Builder spawnDistance(float spawnDistance) {
			this.spawnDistance = spawnDistance;
			return this;
		}

		public Builder knockback(float knockback) {
			this.knockback = knockback;
			return this;
		}

		public Builder hitCount(int hitCount) {
			this.hitCount = hitCount;
			return this;
		}

		public Builder status(float chance, Consumer<DungeonEntity> action) {
			this.statusChance = chance;
			this.statusAction = action;
			return this;
		}

		public AttackModule build() {
			return new AttackModule(this);
		}
	}
}
