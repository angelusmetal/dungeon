package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

public class AcidSlime extends Character {

	private static final float MIN_TARGET_DISTANCE = distance2(300);
	private static final float DASH = distance2(150);
	private static final float POOL_SEPARATION = distance2(15);
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

		setCurrentAnimation(new GameAnimation(factory.idleAnimation, GameState.time()));
		speed = 100f;
		maxHealth = 100 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;
		friction = 2;

		nextThink = 0f;
	}

	@Override
	public void think() {
		if (getSelfImpulse().x != 0) {
			setInvertX(getSelfImpulse().x < 0);
		}
		if (GameState.time() > nextThink) {
			Vector2 target = reTarget().setLength2(DASH);
			if (target.len2() > 0) {
				nextThink = GameState.time() + ATTACK_FREQUENCY;
				// Aim towards target
				impulse(target);
				aim(target);
				setCurrentAnimation(new GameAnimation(factory.attackAnimation, GameState.time()));
				this.status = Status.ATTACKING;
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				speed = 5f;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					setCurrentAnimation(new GameAnimation(factory.idleAnimation, GameState.time()));
				} else {
					setSelfImpulse(Vector2.Zero);
					setCurrentAnimation(new GameAnimation(factory.idleAnimation, GameState.time()));
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

	private Vector2 reTarget() {
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : GameState.getPlayerCharacters()) {
			//TODO Use dst2 instead!
			Vector2 v = playerCharacter.getPos().cpy().sub(getPos());
			float len = v.len2();
			if (len < MIN_TARGET_DISTANCE && (closestPlayer.len2() == 0 || len < closestPlayer.len2())) {
				closestPlayer = v;
			}
		}
		return closestPlayer;
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
