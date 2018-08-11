package com.dungeon.game.character.ghost;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.CreatureEntity;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Timer;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class Ghost extends CreatureEntity {

	private final GhostFactory factory;
	private float visibleUntil = 0;
	private final Timer targettingTimer = new Timer(0.2f);

	Ghost(Vector2 origin, GhostFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		this.health = this.maxHealth *= GameState.getDifficultyTier();
		setCurrentAnimation(factory.idleAnimation);
	}

	@Override
	public void think() {
		super.think();
		// Re-target periodically
		targettingTimer.doAtInterval(() -> {
			ClosestEntity closest = GameState.entities.playerCharacters().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				moveStrictlyTowards(closest.getEntity().getOrigin());
			}
		});
		// Set transparency based on invulnerability
		color.a = Util.clamp(visibleUntil - GameState.time(), 0.1f, 0.5f);
		speed = GameState.time() > visibleUntil ? factory.stealthSpeed : factory.visibleSpeed;
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

	@Override
	public void hit(Attack attack) {
		super.hit(attack);
		visibleUntil = GameState.time() + factory.visibleTime;
	}

	@Override
	public void onExpire() {
		GameState.entities.add(factory.createDeath(getOrigin(), invertX())) ;
		// Create loot
		GameState.createCreatureLoot(getOrigin());
	}

}
