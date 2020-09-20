package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
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

	private static final String IDLE = "slime_fire_idle";

	private static final float MAX_TARGET_DISTANCE = Util.length2(300f);
	private static final float ATTACK_FREQUENCY = 1.5f;
	private static final float ATTACK_SPEED = 50f;
	private static final float IDLE_SPEED = 5f;
	private static final float DAMAGE_PER_HIT = 1f;

	private final Animation<Material> idleAnimation = Resources.animations.get(IDLE);

	private final Weapon weapon;
	private float nextThink;
	private ClosestEntity closest;
	private List<Runnable> actions = Arrays.asList(this::moveCloser, this::leap, this::fireProjectile, this::fireRingAttack);

	public FireSlimeBoss(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		this.health = this.maxHealth *= Game.getDifficultyTier();
		this.drawScale.set(5, 5);
		weapon = new WeaponFactory().buildFireballStaff(Game.getDifficultyTier() * 10f);
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				Rand.pick(actions).run();
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				setSpeed(IDLE_SPEED);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					setSelfImpulse(newDirection);
					updateAnimation(idleAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateAnimation(idleAnimation);
				}
			}
		} else {
			setSpeed(getSpeed() * (1f - 0.5f * Engine.frameTime()));
		}
	}

	private void moveCloser() {
		nextThink = Engine.time() + ATTACK_FREQUENCY;
		// Move towards target
		setSpeed(ATTACK_SPEED);
		moveStrictlyTowards(closest.getEntity().getOrigin());
	}

	private void leap() {
		nextThink = Engine.time() + ATTACK_FREQUENCY * 2;
		// Move towards target
		setSpeed(ATTACK_SPEED * 5);
		moveStrictlyTowards(closest.getEntity().getOrigin());
	}

	private void fireProjectile() {
		nextThink = Engine.time() + ATTACK_FREQUENCY;
		setSpeed(ATTACK_SPEED);
		// Fire a projectile
		Vector2 aim = closest.getEntity().getOrigin().cpy().sub(getOrigin()).setLength(1);
		weapon.attack(getOrigin(), aim);
		shout(attackPhrases, 0.1f);
	}

	private void fireRingAttack() {
		nextThink = Engine.time() + ATTACK_FREQUENCY;
		setSpeed(ATTACK_SPEED);
		// Move towards target
		int bullets = 20;
		Vector2 aim = new Vector2(0, 1);
		for (int i = 0; i < bullets; ++i) {
			weapon.attack(getOrigin(), aim);
			aim.rotate(360f / bullets);
		}
	}

//	@Override
//	protected boolean onEntityCollision(DungeonEntity entity) {
//		if (entity instanceof PlayerEntity) {
//			getSelfImpulse().set(Vector2.Zero);
//			stop();
//			Attack attack = new Attack(this, DAMAGE_PER_HIT * Engine.frameTime(), DamageType.NORMAL, 0);
//			entity.hit(attack);
//			return true;
//		} else {
//			return false;
//		}
//	}

	@Override
	protected void onTileCollision(boolean horizontal) {
		getSelfImpulse().set(Vector2.Zero);
		stop();
	}

}
