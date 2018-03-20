package com.dungeon.game.character;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.Light;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class Ghost extends Character {

	private static final float MIN_TARGET_DISTANCE = 300 * 300;
	static private Light GHOST_LIGHT = new Light(200, new Color(0.2f, 0.4f, 1, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateSlow);
	private float invulnerableUntil = 0;

	public static class Factory implements EntityFactory.EntityTypeFactory {

		private GameState state;
		private AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class);

		public Factory(GameState state) {
			this.state = state;
			provider.register(AnimationType.IDLE, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
			provider.register(AnimationType.WALK, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
			provider.register(AnimationType.ATTACK, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		}

		@Override
		public Entity<?> build(Vector2 origin) {
			Ghost entity = new Ghost(origin);
			entity.setAnimationProvider(provider);
			entity.setCurrentAnimation(provider.get(AnimationType.IDLE, state.getStateTime()));
			entity.speed = 20f;
			entity.light = GHOST_LIGHT;
			entity.maxHealth = 100 * state.getPlayerCount();
			entity.health = entity.maxHealth;
			return entity;
		}
	}

	private final Color color;

	private Ghost(Vector2 pos) {
		super(new Body(pos, new Vector2(16, 30)));
		color = new Color(1, 1, 1, 0.5f);
		drawContext = new ColorContext(color);
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
	protected boolean onEntityCollision(GameState state, Entity<?> entity) {
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
			invulnerableUntil = state.getStateTime() + 1f; // invulnerable for the next 1 seconds
		}
	}

	@Override
	public boolean canBeHit(GameState state) {
		return state.getStateTime() > invulnerableUntil;
	}

}
