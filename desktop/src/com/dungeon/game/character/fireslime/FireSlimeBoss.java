package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.object.weapon.WeaponFactory;

import java.util.Arrays;
import java.util.List;

public class FireSlimeBoss extends CreatureEntity {

	private static final List<String> attackPhrases = Arrays.asList("I'm on fire!", "Eat lead!", "That will teach you", "Smoky!");
	private static final float MAX_TARGET_DISTANCE = 600f;

	private final FireSlimeFactory factory;
	private final Weapon weapon;
	private float nextThink;
	private ClosestEntity closest;
	private List<Runnable> actions = Arrays.asList(this::moveCloser, this::leap, this::fireProjectile, this::fireRingAttack);

	FireSlimeBoss(Vector2 origin, EntityPrototype prototype, FireSlimeFactory factory) {
		super(origin, prototype);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
		this.drawScale.set(5, 5);
		weapon = new WeaponFactory().buildFireballStaff(Game.getDifficultyTier() * 10f);
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				Rand.pick(actions).run();
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

	private void moveCloser() {
		nextThink = Engine.time() + factory.attackFrequency;
		// Move towards target
		speed = factory.attackSpeed;
		moveStrictlyTowards(closest.getEntity().getOrigin());
	}

	private void leap() {
		nextThink = Engine.time() + factory.attackFrequency * 2;
		// Move towards target
		speed = factory.attackSpeed * 5;
		moveStrictlyTowards(closest.getEntity().getOrigin());
	}

	private void fireProjectile() {
		nextThink = Engine.time() + factory.attackFrequency;
		speed = factory.attackSpeed;
		// Fire a projectile
		Vector2 aim = closest.getEntity().getOrigin().cpy().sub(getOrigin()).setLength(1);
		weapon.attack(getOrigin(), aim);
		shout(attackPhrases, 0.1f);
	}

	private void fireRingAttack() {
		nextThink = Engine.time() + factory.attackFrequency;
		speed = factory.attackSpeed;
		// Move towards target
		int bullets = 20;
		Vector2 aim = new Vector2(0, 1);
		for (int i = 0; i < bullets; ++i) {
			weapon.attack(getOrigin(), aim);
			aim.rotate(360f / bullets);
		}
	}

	@Override
	protected boolean onEntityCollision(DungeonEntity entity) {
		if (entity instanceof PlayerEntity) {
			getSelfImpulse().set(Vector2.Zero);
			stop();
			Attack attack = new Attack(this, factory.damagePerSecond * Engine.frameTime(), DamageType.NORMAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onTileCollision(boolean horizontal) {
		getSelfImpulse().set(Vector2.Zero);
		stop();
	}

}