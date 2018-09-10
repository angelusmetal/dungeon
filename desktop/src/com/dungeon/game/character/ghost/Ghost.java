package com.dungeon.game.character.ghost;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Timer;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

public class Ghost extends CreatureEntity {

	private final GhostFactory factory;
	private float visibleUntil = 0;
	private final Timer targettingTimer = new Timer(0.2f);

	Ghost(Vector2 origin, EntityPrototype prototype, GhostFactory factory) {
		super(origin, prototype);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
	}

	@Override
	public void think() {
		super.think();
		// Re-target periodically
		targettingTimer.doAtInterval(() -> {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				moveStrictlyTowards(closest.getEntity().getOrigin());
			}
		});
		// Set transparency based on invulnerability
		color.a = Util.clamp(visibleUntil - Engine.time(), 0.1f, 0.5f);
		speed = Engine.time() > visibleUntil ? factory.stealthSpeed : factory.visibleSpeed;
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
	public void hit(Attack attack) {
		super.hit(attack);
		visibleUntil = Engine.time() + factory.visibleTime;
	}

	@Override
	public void onExpire() {
		// Create loot TODO Move this to the conf file
		Game.createCreatureLoot(getOrigin());
	}

}
