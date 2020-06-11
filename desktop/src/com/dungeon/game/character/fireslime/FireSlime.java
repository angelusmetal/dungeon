package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.player.Players;

import java.util.Arrays;
import java.util.List;

public class FireSlime extends CreatureEntity {

	private static final List<String> attackPhrases = Arrays.asList("I'm on fire!", "Eat lead!", "That will teach you", "Smoky!");

	private final FireSlimeFactory factory;
	private float nextThink;

	FireSlime(Vector2 origin, FireSlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = Engine.time() + factory.attackFrequency;
				// Move towards target
				speed = factory.attackSpeed;
				moveStrictlyTowards(closest.getEntity().getOrigin());
				// Fire a projectile
				Vector2 aim = closest.getEntity().getOrigin().cpy().sub(getOrigin()).setLength(1);
				factory.getWeapon().spawnEntities(getOrigin(), aim);
				shout(attackPhrases, 0.1f);
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				speed = factory.idleSpeed;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					setSelfImpulse(newDirection);
					updateAnimation(factory.idleAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateAnimation(factory.idleAnimation);
				}
			}
		} else {
			speed *= 1 - 0.5 * Engine.frameTime();
		}
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
