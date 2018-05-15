package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Timer;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Util;
import com.dungeon.game.state.GameState;

public class Ghost extends Character {

	private static final float MAX_TARGET_DISTANCE = Util.length2(300);
	private static final float VISIBLE_TIME = 2f;
	private final GhostFactory factory;
	private float visibleUntil = 0;
	private final Timer targettingTimer = new Timer(0.2f);

	Ghost(Vector2 origin, GhostFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		setCurrentAnimation(factory.idleAnimation);
		maxHealth = 100 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;
	}

	@Override
	public void think() {
		super.think();
		// Re-target periodically
		targettingTimer.doAtInterval(() -> {
			ClosestEntity closest = GameState.getPlayerCharacters().stream().collect(() -> new ClosestEntity(this), ClosestEntity::accept, ClosestEntity::combine);
			if (closest.getDst2() < MAX_TARGET_DISTANCE) {
				moveStrictlyTowards(closest.getEntity().getPos());
			}
		});
		// Set transparency based on invulnerability
		color.a = Util.clamp(visibleUntil - GameState.time(), 0.1f, 0.5f);
		speed = GameState.time() > visibleUntil ? 40f : 20f;
	}

	@Override
	protected Animation<TextureRegion> getAttackAnimation() {
		return factory.idleAnimation;
	}

	@Override
	protected Animation<TextureRegion> getIdleAnimation() {
		return factory.idleAnimation;
	}

	@Override
	protected Animation<TextureRegion> getWalkAnimation() {
		return factory.idleAnimation;
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(20 * GameState.frameTime());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void hit(float dmg) {
		super.hit(dmg);
		visibleUntil = GameState.time() + VISIBLE_TIME;
	}

	@Override
	public void onExpire() {
		GameState.addEntity(factory.createDeath(getPos(), invertX())) ;
	}

}
