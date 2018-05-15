package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.state.GameState;

public class SlimeSpawn extends Character {

	private static final float MAX_TARGET_DISTANCE = Util.length2(300);
	private static final float JUMP = Util.length2(50);

	private final SlimeFactory factory;
	private float nextThink;

	SlimeSpawn(Vector2 origin, SlimeFactory factory) {
		super(origin, factory.spawn);
		this.factory = factory;

		setCurrentAnimation(factory.spawnBlinkAnimation);
		maxHealth = 25 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;
		nextThink = 0f;
	}

	@Override
	public void think() {
		if (getAim().x != 0) {
			setInvertX(getAim().x < 0);
		}
		if (GameState.time() > nextThink) {
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				nextThink = GameState.time() + 3f;
				// Aim towards target
				impulseTowards(closest.getEntity().getPos(), JUMP);
				aim(getMovement());
				zSpeed = 100;
				updateCurrentAnimation(factory.spawnIdleAnimation);
			} else {
				nextThink = GameState.time() + Rand.nextFloat(3f);
				// Aim random direction
				if (Rand.chance(0.7f)) {
					Vector2 newDirection = new Vector2(Rand.between(10f, 10), Rand.between(-10f, 10f));
					impulse(newDirection);
					aim(newDirection);
					updateCurrentAnimation(factory.spawnBlinkAnimation);
				} else {
					setSelfImpulse(Vector2.Zero);
					updateCurrentAnimation(factory.spawnBlinkAnimation);
				}
			}
		} else {
			// No friction while in the air
			friction = z > 0 ? 0 : 8;
		}
	}

	@Override
	protected void onGroundRest() {
		updateCurrentAnimation(factory.spawnBlinkAnimation);
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
	protected void onExpire() {
		GameState.addEntity(factory.createSpawnDeath(this));
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
