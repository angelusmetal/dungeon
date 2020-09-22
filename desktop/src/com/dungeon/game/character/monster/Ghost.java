package com.dungeon.game.character.monster;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Metronome;
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

	private static final float ACTIVATION_DISTANCE = Util.length2(40f);
	private static final float MAX_TARGET_DISTANCE = Util.length2(300f);
	private static final float VISIBLE_TIME = 2f;
	private static final float VISIBLE_SPEED = 20f;
	private static final float STEALTH_SPEED = 40f;
	private static final float DAMAGE_PER_HIT = 8f;

	private float visibleUntil = 0;
	private boolean active = false;
	private final Metronome targettingMetronome;

	public Ghost(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		this.health = this.maxHealth *= Game.getDifficultyTier();
		this.targettingMetronome = new Metronome(0.2f, () -> {
			ClosestEntity closest = Engine.entities.ofType(PlayerEntity.class).collect(() -> ClosestEntity.to(this), ClosestEntity::accept, ClosestEntity::combine);
			if (active) {
				if (closest.getDst2() < MAX_TARGET_DISTANCE) {
					selfImpulseStrictlyTowards(closest.getEntity().getOrigin());
					shout(attackPhrases, 0.02f);
				}
			} else {
				if (closest.getDst2() < ACTIVATION_DISTANCE) {
					selfImpulseStrictlyTowards(closest.getEntity().getOrigin());
					shout(attackPhrases, 0.02f);
					active = true;
				}
			}
		});
	}

	@Override
	public void think() {
		super.think();
		// Re-target periodically
		targettingMetronome.doAtInterval();
		// Set transparency based on active / recent damage taken
		color.a = active ? Util.clamp(visibleUntil - Engine.time(), 0.4f, 0.8f) : 0f;
		setSpeed(Engine.time() > visibleUntil ? STEALTH_SPEED : VISIBLE_SPEED);
	}

	@Override
	protected boolean onEntityCollision(DungeonEntity entity) {
		if (entity instanceof PlayerEntity) {
			Attack attack = new Attack(this, DAMAGE_PER_HIT, DamageType.NORMAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void hit(Attack attack) {
		super.hit(attack);
		active = true;
		visibleUntil = Engine.time() + VISIBLE_TIME;
		shout(hitPhrases, 0.3f);
	}

}
