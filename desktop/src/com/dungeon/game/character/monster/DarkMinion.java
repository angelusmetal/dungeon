package com.dungeon.game.character.monster;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Util;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.PlayerEntity;

import java.util.LinkedList;
import java.util.function.Consumer;

public class DarkMinion extends CreatureEntity {

	private static final float MAX_TARGET_DISTANCE = Util.length2(240f);
	private static final float ATTACK_DISTANCE = Util.length2(32f);
	private static final float TARGET_FREQUENCY = 0.2f;
	private static final float ATTACK_FREQUENCY = 1f;
	private static final float ATTACK_LENGTH = 0.36f;
	private static final float ATTACK_TELL = 1f;

	private final Animation<Material> idleAnimation = Resources.animations.get("dark_minion_idle");
	private final Animation<Material> walkAnimation = Resources.animations.get("dark_minion_walk");
	private final Animation<Material> attackAnimation = Resources.animations.get("dark_minion_attack");
	private final Animation<Material> attackTellAnimation = Resources.animations.get("dark_minion_attack_tell");
	private final Weapon weapon;

	private float nextThink;

	static class Behavior {
		float duration;
		Runnable action;
		public Behavior(float duration, Runnable action) {
			this.duration = duration;
			this.action = action;
		}
	}

	LinkedList<Behavior> scheduled = new LinkedList<>();

	public DarkMinion(Vector2 origin, EntityPrototype prototype, Weapon weapon) {
		super(origin, prototype);
		this.weapon = weapon;
	}

	@Override
	public void think() {
		if (Engine.time() >= nextThink) {
			if (!scheduled.isEmpty()) {
				Behavior next = scheduled.pollFirst();
				nextThink = Engine.time() + next.duration;
				next.action.run();
			} else {
				pickBehavior();
			}
		}
	}

	private void pickBehavior() {
		ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
		if (closest.getDst2() < ATTACK_DISTANCE) {
			nextThink = Engine.time() + ATTACK_TELL;
			setSelfImpulse(0f,0f);
			aimTo(closest.getEntity().getOrigin());
			updateAnimation(attackTellAnimation);
			scheduled.add(new Behavior(ATTACK_LENGTH, () -> {
				updateAnimation(attackAnimation);
				weapon.attack(getBody().getCenter(), getAim());
			}));
			scheduled.add(new Behavior(ATTACK_FREQUENCY - ATTACK_LENGTH, () -> {
				updateAnimation(idleAnimation);
			}));
		} else if (closest.getDst2() < MAX_TARGET_DISTANCE) {
			nextThink = Engine.time() + TARGET_FREQUENCY;
			moveStrictlyTowards(closest.getEntity().getOrigin());
			aimTo(closest.getEntity().getOrigin());
			updateAnimation(walkAnimation);
		} else {
			nextThink = Engine.time() + TARGET_FREQUENCY;
			updateAnimation(idleAnimation);
		}
	}

}
