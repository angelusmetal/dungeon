package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.PlayerEntity;

import java.util.Arrays;
import java.util.List;

public class AcidSlime extends CreatureEntity {

	private static final List<String> attackPhrases = Arrays.asList("Caustic!", "Slimy!", "I'm toxic!");

	private static final String IDLE = "slime_acid_idle";
	private static final String ATTACK = "slime_acid_attack";

	private static final float MAX_TARGET_DISTANCE = Util.length2(300f);
	private static final float DASH_DISTANCE = Util.length2(150f);
	private static final float POOL_SEPARATION = 15f;
	private static final float ATTACK_FREQUENCY = 3f;

	private final Animation<Material> idleAnimation = Resources.animations.get(IDLE);
	private final Animation<Material> attackAnimation = Resources.animations.get(ATTACK);

	private final EntityTypeFactory poolFactory;
	private final Vector2 lastPool = new Vector2(0,0);
	private float nextThink;
	private enum Status {
		IDLE, ATTACKING
	}
	private Status status;

	public AcidSlime(Vector2 origin, EntityPrototype prototype, EntityTypeFactory poolFactory) {
		super(origin, prototype);
		this.poolFactory = poolFactory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
	}

	@Override
	public void think() {
		if (Engine.time() > nextThink) {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				nextThink = Engine.time() + ATTACK_FREQUENCY;
				// Aim towards target
				impulseTowards(closest.getEntity().getOrigin(), DASH_DISTANCE);
				aim(getMovement());
				updateAnimation(attackAnimation);
				this.status = Status.ATTACKING;
				shout(attackPhrases, 0.02f);
			} else {
				nextThink = Engine.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
				} else {
					setSelfImpulse(Vector2.Zero);
				}
				updateAnimation(idleAnimation);
				this.status = Status.IDLE;
			}
		} else {
			if (status == Status.ATTACKING) {
				if (getOrigin().dst2(lastPool) > POOL_SEPARATION) {
					lastPool.set(getOrigin());
					Engine.entities.add(poolFactory.build(getOrigin()));
				}
				if (Engine.time() >= nextThink - 2) {
					updateAnimation(idleAnimation);
				}
			}
		}
	}

//	@Override
//	protected boolean onEntityCollision(DungeonEntity entity) {
//		if (entity instanceof PlayerEntity) {
//			Attack attack = new Attack(this, factory.damagePerSecond * Engine.frameTime(), DamageType.NORMAL, 0);
//			entity.hit(attack);
//			return true;
//		} else {
//			return false;
//		}
//	}

}
