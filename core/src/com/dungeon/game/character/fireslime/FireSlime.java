package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.character.acidslime.AcidSlime;
import com.dungeon.game.state.GameState;

public class FireSlime extends Character {

	private static final float MAX_TARGET_DISTANCE = Util.length2(300);
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
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				nextThink = GameState.time() + ATTACK_FREQUENCY;
				// Move towards target
				speed = ATTACK_SPEED;
				moveStrictlyTowards(closest.getEntity().getPos());
				// Fire a projectile
				Entity bullet = factory.createBullet(getPos());
				bullet.moveStrictlyTowards(closest.getEntity().getPos());
				GameState.addEntity(bullet);
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				speed = 5f;
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(-10f, 10f), Rand.between(-10f, 10f));
					setSelfImpulse(newDirection);
					updateCurrentAnimation(factory.idleAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateCurrentAnimation(factory.idleAnimation);
				}
			}
		} else {
			speed *= 1 - 0.5 * GameState.frameTime();
		}
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
