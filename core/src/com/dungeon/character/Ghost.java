package com.dungeon.character;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;

public class Ghost extends Character {

	private static final float MIN_TARGET_DISTANCE = 500 * 500;

	public Ghost(GameState state) {
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.IDLE, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.WALK, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.JUMP, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.HIT, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.SLASH, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.PUNCH, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.RUN, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		provider.register(AnimationType.CLIMB, state.getTilesetManager().getGhostTileset().HOVER_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(AnimationType.IDLE));
		setHitBox(16, 30);
		maxSpeed = 1f;
	}

	@Override
	public void think(GameState state) {
		Vector2 closestPlayer = new Vector2();
		for (PlayerCharacter playerCharacter : state.getPlayerCharacters()) {
			Vector2 v = playerCharacter.getPos().cpy().sub(getPos());
			float len = v.len2();
			if (len < MIN_TARGET_DISTANCE && (closestPlayer.len2() == 0 || len < closestPlayer.len2())) {
				closestPlayer = v;
			}
			setSelfMovement(closestPlayer);
		}
	}

	@Override
	protected void onEntityCollision(GameState state, Entity<?> entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, 1);
		}
	}
}
