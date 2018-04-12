package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class AcidSlime extends Character {

	private static final Vector2 BOUNDING_BOX = new Vector2(22, 12);
	private static final float MIN_TARGET_DISTANCE = distance2(300);
	private static final float POOL_SEPARATION = distance2(15);
	private static final float ATTACK_FREQUENCY = 3f;
	private static final float ATTACK_SPEED = 75f;

	private final AcidSlimeFactory factory;
	private final Vector2 lastPool = new Vector2(0,0);
	private float nextThink;
	private enum Status {
		IDLE, ATTACKING
	}
	private Status status;

	AcidSlime(AcidSlimeFactory factory, Vector2 pos) {
		super(new Body(pos, BOUNDING_BOX));
		this.factory = factory;

		setCurrentAnimation(new GameAnimation(factory.idleAnimation, factory.state.getStateTime()));
		speed = 20f;
		light = factory.characterLight;
		maxHealth = 100 * (factory.state.getPlayerCount() + factory.state.getLevelCount());
		health = maxHealth;

		nextThink = 0f;
		drawContext = factory.drawContext;
	}

	@Override
	public void think(GameState state) {
//		super.think(state);
		if (getSelfImpulse().x != 0) {
			setInvertX(getSelfImpulse().x < 0);
		}
		if (state.getStateTime() > nextThink) {
			Vector2 target = reTarget(state);
			if (target.len2() > 0) {
				nextThink = state.getStateTime() + ATTACK_FREQUENCY;
				// Aim towards target
				speed = ATTACK_SPEED;
				setSelfImpulse(target);
				setCurrentAnimation(new GameAnimation(factory.attackAnimation, state.getStateTime()));
				this.status = Status.ATTACKING;
			} else {
				nextThink = state.getStateTime() + (float) Math.random() * 3f;
				speed = 5f;
				// Aim random direction
				if (Math.random() > 0.7f) {
					Vector2 newDirection = new Vector2((float)Math.random() * 20f - 10f, (float)Math.random() * 20f - 10f);
					setSelfImpulse(newDirection);
					setCurrentAnimation(new GameAnimation(factory.idleAnimation, state.getStateTime()));
				} else {
					setSelfImpulse(Vector2.Zero);
					setCurrentAnimation(new GameAnimation(factory.idleAnimation, state.getStateTime()));
				}
				this.status = Status.IDLE;
			}
		} else {
			speed *= 1 - 0.5 * state.getFrameTime();
			if (status == Status.ATTACKING && getPos().dst2(lastPool) > POOL_SEPARATION) {
				lastPool.set(getPos());
				state.addEntity(new AcidPool(factory, state, getPos()));
			}
		}
	}

	private Vector2 reTarget(GameState state) {
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
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
	protected void onExpire(GameState state) {
		// Create a death splatter and a pool
		state.addEntity(new DieSplatter(factory, state, getPos()));
		state.addEntity(new AcidPool(factory, state, getPos()));
		// Create 5-10 blobs
		int splats = (int) (15 + Math.random() * 10);
		for (int i = 0; i < splats; ++i) {
			state.addEntity(new AcidBlob(factory, state, getPos()));
		}
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 10 * state.getFrameTime());
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
