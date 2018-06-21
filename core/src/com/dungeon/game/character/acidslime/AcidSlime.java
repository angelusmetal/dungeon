package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.CreatureEntity;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

public class AcidSlime extends CreatureEntity {

	private final AcidSlimeFactory factory;
	private final Vector2 lastPool = new Vector2(0,0);
	private float nextThink;
	private enum Status {
		IDLE, ATTACKING
	}
	private Status status;

	AcidSlime(Vector2 origin, AcidSlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;

		setCurrentAnimation(factory.idleAnimation);
	}

	@Override
	public void think() {
		if (getMovement().x != 0) {
			setInvertX(getMovement().x < 0);
		}
		if (GameState.time() > nextThink) {
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < factory.maxTargetDistance) {
				nextThink = GameState.time() + factory.attackFrequency;
				// Aim towards target
				impulseTowards(closest.getEntity().getPos(), factory.dashDistance);
				aim(getMovement());
				updateCurrentAnimation(factory.attackAnimation);
				this.status = Status.ATTACKING;
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				speed = 5f;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateCurrentAnimation(factory.idleAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateCurrentAnimation(factory.idleAnimation);
				}
				this.status = Status.IDLE;
			}
		} else {
			if (status == Status.ATTACKING) {
				if (getPos().dst2(lastPool) > factory.poolSeparation) {
					lastPool.set(getPos());
					GameState.addEntity(factory.createPool(this));
				}
				if (GameState.time() >= nextThink - 2) {
					updateCurrentAnimation(factory.idleAnimation);
				}
			}
		}
	}

	@Override
	protected void onExpire() {
		// Create a death splatter and a pool
		GameState.addEntity(factory.createDeath(this));
		GameState.addEntity(factory.createPool(this));
		// Create a bunch of blobs
		int splats = Rand.between(8, 16);
		for (int i = 0; i < splats; ++i) {
			GameState.addEntity(factory.createBlob(this));
		}
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
	protected void onHit() {
		GameState.addEntity(factory.createBlob(this));
	}

}
