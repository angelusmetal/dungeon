package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

public class SlimeSpawn extends CreatureEntity {

	private static final String SPAWN_IDLE = "slime_mini_idle";
	private static final String SPAWN_BLINK = "slime_mini_blink";

	private static final float MAX_TARGET_DISTANCE = Util.length2(300);
	private static final float JUMP_DISTANCE = Util.length2(50);
	private static final float DAMAGE_PER_SECOND = 1;
	private static final float ATTACK_FREQUENCY = 3;

	private final Animation<Material> spawnIdleAnimation = Resources.animations.get(SPAWN_IDLE);
	private final Animation<Material> spawnBlinkAnimation = Resources.animations.get(SPAWN_BLINK);

	private float nextThink;

	SlimeSpawn(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		startAnimation(spawnBlinkAnimation);
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				nextThink = Engine.time() + ATTACK_FREQUENCY;
				// Aim towards target
				impulseTowards(closest.getEntity().getOrigin(), JUMP_DISTANCE);
				aim(getMovement());
				zSpeed = 100;
				updateAnimation(spawnIdleAnimation);
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateAnimation(spawnBlinkAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateAnimation(spawnBlinkAnimation);
				}
			}
		}
	}

	@Override
	protected void onGroundRest() {
		updateAnimation(spawnBlinkAnimation);
	}

	@Override
	protected boolean onEntityCollision(DungeonEntity entity) {
		if (entity instanceof PlayerEntity) {
			Attack attack = new Attack(this, DAMAGE_PER_SECOND * Engine.frameTime(), DamageType.NORMAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}

}
