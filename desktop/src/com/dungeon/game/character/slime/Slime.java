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

import java.util.Arrays;
import java.util.List;

public class Slime extends CreatureEntity {

	private static final List<String> attackPhrases = Arrays.asList("Let's play!", "Jell-o!", "Eat me!");

	private final SlimeFactory factory;
	private float nextThink;
	Slime(Vector2 origin, EntityPrototype prototype, SlimeFactory factory) {
		super(origin, prototype);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
		setCurrentAnimation(factory.blinkAnimation);
	}

	@Override
	public void think() {
		if (getAim().x != 0) {
			setInvertX(getAim().x < 0);
		}
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = Engine.time() + factory.attackFrequency;
				impulseTowards(closest.getEntity().getOrigin(), factory.jumpDistance);
				aim(getMovement());
				zSpeed = 100;
				updateCurrentAnimation(factory.idleAnimation);
				shout(attackPhrases, 0.02f);
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateCurrentAnimation(factory.blinkAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateCurrentAnimation(factory.blinkAnimation);
				}
			}
		} else {
			// No friction while in the air
			friction = z > 0 ? 0 : 8;
		}
	}

	@Override
	protected void onGroundRest() {
		updateCurrentAnimation(factory.blinkAnimation);
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

	@Override
	protected void onExpire() {
		// TODO Move this as well to conf
		Game.createCreatureLoot(getOrigin());
	}

}
