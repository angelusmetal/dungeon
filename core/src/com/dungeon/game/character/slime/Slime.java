package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

public class Slime extends Character {

	private static final float MIN_TARGET_DISTANCE = distance2(300);
	private static final float JUMP = distance2(50);

	private final SlimeFactory factory;
	private float nextThink;
	private enum Status {
		IDLE, ATTACKING
	}
	private Status status;
	/** Vertical speed */
	private float zSpeed;
	/** Vertical acceleration */
	private final float zAcceleration;

	Slime(Vector2 origin, SlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;

		setCurrentAnimation(new GameAnimation(factory.idleAnimation, GameState.time()));
		maxHealth = 75 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;

		nextThink = 0f;

		zAcceleration = -200;
	}

	@Override
	public void think() {
//		super.think(state);
		if (getAim().x != 0) {
			setInvertX(getAim().x < 0);
		}
		if (GameState.time() > nextThink) {
			Vector2 target = reTarget().setLength2(JUMP);
			if (target.len2() > 0) {
				// Attack lasts 2 seconds
				nextThink = GameState.time() + 3f;
				// Aim towards target
				impulse(target);
				aim(target);
				zSpeed = 100;
				setCurrentAnimation(new GameAnimation(factory.attackAnimation, GameState.time()));
				this.status = Status.ATTACKING;
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
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
			zSpeed += zAcceleration * GameState.frameTime();
			z += zSpeed * GameState.frameTime();
			if (z < 0) {
				z = 0;
			}
			// No friction while in the air
			friction = z > 0 ? 0 : 8;
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

//	@Override
//	protected void onExpire(GameState state) {
//		// Create a death splatter and a pool
//		state.addEntity(new DieSplatter(factory, state, getPos()));
//		state.addEntity(new AcidPool(factory, state, getPos()));
//		// Create 5-10 blobs
//		int splats = (int) (5 + Math.random() * 5);
//		for (int i = 0; i < splats; ++i) {
//			state.addEntity(new AcidBlob(factory, state, getPos()));
//		}
//	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(10 * GameState.frameTime());
			return true;
		} else {
			return false;
		}
	}

	// TODO This should not be here: either Character should not enforce this or this should not extend character
	@Override protected Animation<TextureRegion> getAttackAnimation() {return null;}
	@Override protected Animation<TextureRegion> getIdleAnimation() {return null;}
	@Override protected Animation<TextureRegion> getWalkAnimation() {return null;}

}
