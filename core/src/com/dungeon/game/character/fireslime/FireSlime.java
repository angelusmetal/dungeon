package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

public class FireSlime extends Character {

	private static final float MIN_TARGET_DISTANCE = distance2(300);
	private static final float ATTACK_FREQUENCY = 1.5f;
	private static final float ATTACK_SPEED = 10f;

	private final FireSlimeFactory factory;
	private float nextThink;

	FireSlime(Vector2 origin, FireSlimeFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		maxHealth = 50 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;
		nextThink = 0f;
	}

	@Override
	public void think() {
		if (getSelfImpulse().x != 0) {
			setInvertX(getSelfImpulse().x < 0);
		}
		if (GameState.time() > nextThink) {
			Vector2 target = reTarget();
			if (target.len2() > 0) {
				nextThink = GameState.time() + ATTACK_FREQUENCY;
				// Move towards target
				speed = ATTACK_SPEED;
				setSelfImpulse(target);
				// Fire a projectile
				Entity bullet = factory.createBullet(getPos());
				bullet.setSelfImpulse(target);
				GameState.addEntity(bullet);
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				speed = 5f;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					setSelfImpulse(newDirection);
					setCurrentAnimation(new GameAnimation(factory.idleAnimation, GameState.time()));
				} else {
					setSelfImpulse(Vector2.Zero);
					setCurrentAnimation(new GameAnimation(factory.idleAnimation, GameState.time()));
				}
			}
		} else {
			speed *= 1 - 0.5 * GameState.frameTime();
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
		int bullets = (GameState.getPlayerCount() + GameState.getLevelCount()) * 2;
		for (int i = 0; i < bullets; ++i) {
			Entity bullet = factory.createBullet(getPos());
			bullet.setSelfImpulse(0, 1);
			bullet.getSelfImpulse().rotate(360 / bullets * i);
			GameState.addEntity(bullet);
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

	// TODO This should not be here: either Character should not enforce this or this should not extend character
	@Override protected Animation<TextureRegion> getAttackAnimation() {return null;}
	@Override protected Animation<TextureRegion> getIdleAnimation() {return null;}
	@Override protected Animation<TextureRegion> getWalkAnimation() {return null;}

}
