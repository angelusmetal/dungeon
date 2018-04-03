package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.game.state.GameState;

public class Ghost extends Character {

	private static final float MIN_TARGET_DISTANCE = distance2(300);
	private static final float INVULNERABILITY_TIME = 0.5f;
	private final GhostFactory factory;
	private final Color color;
	private float invulnerableUntil = 0;

	Ghost(GhostFactory factory, Vector2 pos) {
		super(new Body(pos, new Vector2(16, 30)));
		this.factory = factory;
		color = new Color(1, 1, 1, 0.5f);
		drawContext = new ColorContext(color);
		setCurrentAnimation(new GameAnimation(getIdleAnimation(), factory.state.getStateTime()));
		speed = 20f;
		light = factory.characterLight;
		maxHealth = 100 * (factory.state.getPlayerCount() + factory.state.getLevelCount());
		health = maxHealth;
	}

	@Override
	public void think(GameState state) {
		super.think(state);
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
			Vector2 v = playerCharacter.getPos().cpy().sub(getPos());
			float len = v.len2();
			if (len < MIN_TARGET_DISTANCE && (closestPlayer.len2() == 0 || len < closestPlayer.len2())) {
				closestPlayer = v;
			}
			setSelfMovement(closestPlayer);
		}
		// Set transparency based on invulnerability
		color.a = state.getStateTime() > invulnerableUntil ? 0.5f : 0.2f;
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
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 20 * state.getFrameTime());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void hit(GameState state, float dmg) {
		if (canBeHit(state)) {
			super.hit(state, dmg);
			invulnerableUntil = state.getStateTime() + INVULNERABILITY_TIME;
		}
	}

	@Override
	public boolean canBeHit(GameState state) {
		return state.getStateTime() > invulnerableUntil;
	}

}
