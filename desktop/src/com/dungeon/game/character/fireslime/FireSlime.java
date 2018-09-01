package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.Game;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.engine.Engine;
import com.dungeon.game.player.Players;

public class FireSlime extends CreatureEntity {

	private final FireSlimeFactory factory;
	private float nextThink;

	FireSlime(Vector2 origin, FireSlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
	}

	@Override
	public void think() {
		if (getSelfImpulse().x != 0) {
			setInvertX(getSelfImpulse().x < 0);
		}
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = Engine.time() + factory.attackFrequency;
				// Move towards target
				speed = factory.attackSpeed;
				impulseTowards(closest.getEntity().getOrigin(), speed);
				// Fire a projectile
				Vector2 aim = closest.getEntity().getOrigin().cpy().sub(getOrigin()).setLength(1);
				factory.getWeapon().spawnEntities(getOrigin(), aim);
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				speed = factory.idleSpeed;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					setSelfImpulse(newDirection);
					updateCurrentAnimation(factory.idleAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateCurrentAnimation(factory.idleAnimation);
				}
			}
		} else {
			speed *= 1 - 0.5 * Engine.frameTime();
		}
	}

	@Override
	protected void onExpire() {
		int bullets = (Players.count() + Game.getLevelCount()) * 2;
		Vector2 aim = new Vector2(0, 1);
		for (int i = 0; i < bullets; ++i) {
			factory.getWeapon().spawnEntities(getOrigin(), aim);
			aim.rotate(360 / bullets);
		}
		// Create loot
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
