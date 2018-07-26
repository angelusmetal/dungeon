package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.CreatureEntity;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.state.GameState;

public class FireSlime extends CreatureEntity {

	private final FireSlimeFactory factory;
	private float nextThink;

	FireSlime(Vector2 origin, FireSlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		this.health = this.maxHealth *= GameState.getDifficultyTier();
	}

	@Override
	public void think() {
		if (getSelfImpulse().x != 0) {
			setInvertX(getSelfImpulse().x < 0);
		}
		if (GameState.time() > nextThink) {
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = GameState.time() + factory.attackFrequency;
				// Move towards target
				speed = factory.attackSpeed;
				impulseTowards(closest.getEntity().getPos(), speed);
				// Fire a projectile
				Vector2 aim = closest.getEntity().getPos().cpy().sub(getPos()).setLength(1);
				factory.getWeapon().spawnEntities(getPos(), aim);
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
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
			speed *= 1 - 0.5 * GameState.frameTime();
		}
	}

	@Override
	protected void onExpire() {
		int bullets = (GameState.getPlayerCount() + GameState.getLevelCount()) * 2;
		Vector2 aim = new Vector2(0, 1);
		for (int i = 0; i < bullets; ++i) {
			factory.getWeapon().spawnEntities(getPos(), aim);
			aim.rotate(360 / bullets);
		}
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof PlayerEntity) {
			Attack attack = new Attack(this, factory.damagePerSecond * GameState.frameTime(), DamageType.NORMAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}

}
