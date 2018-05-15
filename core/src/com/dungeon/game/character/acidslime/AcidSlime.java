package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.character.slime.Slime;
import com.dungeon.game.state.GameState;

public class AcidSlime extends Character {

	private static final float MAX_TARGET_DISTANCE = Util.length2(300);
	private static final float DASH = Util.length2(150);
	private static final float POOL_SEPARATION = Util.length2(15);
	private static final float ATTACK_FREQUENCY = 3f;

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
		maxHealth = 100 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;
		nextThink = 0f;
	}

	@Override
	public void think() {
		if (getSelfImpulse().x != 0) {
			setInvertX(getSelfImpulse().x < 0);
		}
		if (GameState.time() > nextThink) {
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				nextThink = GameState.time() + ATTACK_FREQUENCY;
				// Aim towards target
				impulseTowards(closest.getEntity().getPos(), DASH);
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
			if (status == Status.ATTACKING && getPos().dst2(lastPool) > POOL_SEPARATION) {
				lastPool.set(getPos());
				GameState.addEntity(factory.createPool(this));
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
		if (entity instanceof PlayerCharacter) {
			entity.hit(10 * GameState.frameTime());
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onHit() {
		GameState.addEntity(factory.createBlob(this));
	}

	// TODO This should not be here: either Character should not enforce this or this should not extend character
	@Override protected Animation<TextureRegion> getAttackAnimation() {return null;}
	@Override protected Animation<TextureRegion> getIdleAnimation() {return null;}
	@Override protected Animation<TextureRegion> getWalkAnimation() {return null;}

}
