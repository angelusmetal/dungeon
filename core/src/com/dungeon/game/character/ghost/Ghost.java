package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.Util;
import com.dungeon.game.state.GameState;

public class Ghost extends Character {

	private static final float MIN_TARGET_DISTANCE = distance2(300);
	private static final float VISIBLE_TIME = 2f;
	private final GhostFactory factory;
	private float visibleUntil = 0;

	private enum Status {
		IDLE, ATTACKING
	}

	Ghost(Vector2 origin, GhostFactory factory) {
		super(origin, factory.character);
		this.factory = factory;
		setCurrentAnimation(new GameAnimation(getIdleAnimation(), GameState.time()));
		maxHealth = 100 * (GameState.getPlayerCount() + GameState.getLevelCount());
		health = maxHealth;
	}

	private static final Vector2 target = new Vector2();

	@Override
	public void think() {
		super.think();
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : GameState.getPlayerCharacters()) {
			target.set(playerCharacter.getPos()).sub(getPos());
			float len = target.len2();
			if (len < MIN_TARGET_DISTANCE && (closestPlayer.len2() == 0 || len < closestPlayer.len2())) {
				closestPlayer = target;
			}
			setSelfImpulse(closestPlayer);
		}
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
