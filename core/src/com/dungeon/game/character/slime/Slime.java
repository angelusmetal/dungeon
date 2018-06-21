package com.dungeon.game.character.slime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.CreatureEntity;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

public class Slime extends CreatureEntity {

	private final SlimeFactory factory;
	private float nextThink;
	Slime(Vector2 origin, SlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		setCurrentAnimation(factory.blinkAnimation);
	}

	@Override
	public void think() {
		if (getAim().x != 0) {
			setInvertX(getAim().x < 0);
		}
		if (GameState.time() > nextThink) {
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = GameState.time() + factory.attackFrequency;
				impulseTowards(closest.getEntity().getPos(), factory.jumpDistance);
				aim(getMovement());
				zSpeed = 100;
				updateCurrentAnimation(factory.idleAnimation);
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateCurrentAnimation(factory.blinkAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateCurrentAnimation(factory.blinkAnimation);
				}
			}
		} else {
			// No friction while in the air
			friction = z > 0 ? 0 : 8;
		}
	}

	@Override
	protected void onGroundRest() {
		updateCurrentAnimation(factory.blinkAnimation);
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof PlayerEntity) {
			entity.hit(factory.damagePerSecond * GameState.frameTime());
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onExpire() {
		GameState.addEntity(factory.createDeath(this));
		// Create a bunch of blobs
		Rand.doBetween(factory.blobsOnDeath / 2, factory.blobsOnDeath, () ->
				GameState.addEntity(factory.createBlob(this)));
		Rand.doBetween(0, 1, () ->
				GameState.addEntity(factory.createSpawn(this)));
	}

	@Override
	protected void onHit() {
		GameState.addEntity(factory.createBlob(this));
	}

}
