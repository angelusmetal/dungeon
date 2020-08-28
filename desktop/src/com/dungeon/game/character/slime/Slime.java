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

	private static final String IDLE = "slime_idle";
	private static final String BLINK = "slime_blink";

	private static final float MAX_TARGET_DISTANCE = Util.length2(300);
	private static final float JUMP_DISTANCE = Util.length2(50);
	private static final float DAMAGE_PER_HIT = 10;
	private static final float ATTACK_FREQUENCY = 3;

	private final Animation<Material> idleAnimation = Resources.animations.get(IDLE);
	private final Animation<Material> blinkAnimation = Resources.animations.get(BLINK);

	private float nextThink;

	Slime(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		this.health = this.maxHealth *= Game.getDifficultyTier();
		startAnimation(blinkAnimation);

		// Set random color on slime and light
		color.set(Util.hsvaToColor(
				Rand.between(0f, 1f),
				1f,
				Rand.between(0.7f, 1f),
				0.7f));
		getLight().color.set(getColor());

	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				nextThink = Engine.time() + ATTACK_FREQUENCY;
				impulseTowards(closest.getEntity().getOrigin(), JUMP_DISTANCE);
				aim(getMovement());
				zSpeed = 100;
				updateAnimation(idleAnimation);
				shout(attackPhrases, 0.02f);
			} else {
				nextThink = Engine.time() + 1f + Rand.nextFloat(2f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateAnimation(blinkAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateAnimation(blinkAnimation);
				}
			}
		}
	}

	@Override
	protected void onGroundRest() {
		updateAnimation(blinkAnimation);
	}

	@Override
	protected boolean onEntityCollision(DungeonEntity entity) {
		if (z > 0 && entity instanceof PlayerEntity) {
			Attack attack = new Attack(this, DAMAGE_PER_HIT, DamageType.NORMAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}

}
