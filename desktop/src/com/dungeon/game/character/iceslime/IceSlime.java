package com.dungeon.game.character.iceslime;

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

import java.util.Arrays;
import java.util.List;

public class IceSlime extends CreatureEntity {

	private static final List<String> attackPhrases = Arrays.asList("Chill out", "Cool", "Caught a cold");

	private final IceSlimeFactory factory;
	private final Vector2 lastPool = new Vector2(0,0);
	private float nextThink;
	private enum Status {
		IDLE, ATTACKING
	}
	private Status status;

	IceSlime(Vector2 origin, EntityPrototype prototype, IceSlimeFactory factory) {
		super(origin, prototype);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = Engine.time() + factory.attackFrequency;
				// Aim towards target
				impulseTowards(closest.getEntity().getOrigin(), factory.dashDistance);
				aim(getMovement());
//				updateCurrentAnimation(factory.attackAnimation);
				this.status = Status.ATTACKING;
				shout(attackPhrases, 0.02f);
			} else {
				nextThink = Engine.time() + Rand.between(2, 3);
				speed = 5f;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
				} else {
					setSelfImpulse(Vector2.Zero);
				}
				updateCurrentAnimation(factory.idleAnimation);
				this.status = Status.IDLE;
			}
		} else {
			if (status == Status.ATTACKING) {
				if (getOrigin().dst2(lastPool) > factory.poolSeparation) {
					lastPool.set(getOrigin());
					Engine.entities.add(factory.pool.build(getOrigin()));
				}
				if (Engine.time() >= nextThink - 2) {
					updateCurrentAnimation(factory.idleAnimation);
				}
			}
		}
	}

	@Override
	protected void onHit() {
		updateCurrentAnimation(factory.hitAnimation);
	}

	@Override
	protected void onExpire() {
		Game.createCreatureLoot(getOrigin());
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
