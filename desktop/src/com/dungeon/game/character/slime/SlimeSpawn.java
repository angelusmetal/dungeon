package com.dungeon.game.character.slime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

public class SlimeSpawn extends CreatureEntity {

	private final SlimeFactory factory;
	private float nextThink;

	SlimeSpawn(Vector2 origin, EntityPrototype prototype, SlimeFactory factory) {
		super(origin, prototype);
		this.factory = factory;
		startAnimation(factory.spawnBlinkAnimation);
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = Engine.time() + factory.attackFrequency;
				// Aim towards target
				impulseTowards(closest.getEntity().getOrigin(), factory.jumpDistance);
				aim(getMovement());
				zSpeed = 100;
				updateAnimation(factory.spawnIdleAnimation);
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateAnimation(factory.spawnBlinkAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateAnimation(factory.spawnBlinkAnimation);
				}
			}
		}
	}

	@Override
	protected void onGroundRest() {
		updateAnimation(factory.spawnBlinkAnimation);
	}

	@Override
	protected boolean onEntityCollision(DungeonEntity entity) {
		if (entity instanceof PlayerEntity) {
			Attack attack = new Attack(this, factory.damagePerSecond * Engine.frameTime(), DamageType.NORMAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}

}
