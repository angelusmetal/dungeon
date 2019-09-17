package com.dungeon.game.character.ghost;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

import java.util.Arrays;
import java.util.List;

public class Ghost extends CreatureEntity {

	private static final List<String> attackPhrases = Arrays.asList("Boo!", "I see dead people...", "Turn back!", "Yum!");
	private static final List<String> hitPhrases = Arrays.asList("Ouch!", "Who you gonna call?", "That was mean!");

	private final GhostFactory factory;
	private float visibleUntil = 0;
	private final Metronome targettingMetronome;

	Ghost(Vector2 origin, EntityPrototype prototype, GhostFactory factory) {
		super(origin, prototype);
		this.factory = factory;
		this.health = this.maxHealth *= Game.getDifficultyTier();
		this.targettingMetronome = new Metronome(0.2f, () -> {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				moveStrictlyTowards(closest.getEntity().getOrigin());
			}
			shout(attackPhrases, 0.02f);
		});
	}

	@Override
	public void think() {
		super.think();
		// Re-target periodically
		targettingMetronome.doAtInterval();
		// Set transparency based on invulnerability
		color.a = Util.clamp(visibleUntil - Engine.time(), 0.4f, 0.8f);
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
		shout(hitPhrases, 0.3f);
	}

	@Override
	public void onExpire() {
		// Create loot TODO Move this to the conf file
		Game.createCreatureLoot(getOrigin());
	}

}
