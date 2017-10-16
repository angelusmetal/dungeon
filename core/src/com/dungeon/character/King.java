package com.dungeon.character;

import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;

public class King extends Character {

	public King(GameState state) {
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.IDLE, state.getTilesetManager().getCharactersTileset().KING_IDLE_ANIMATION);
		provider.register(AnimationType.WALK, state.getTilesetManager().getCharactersTileset().KING_WALK_ANIMATION);
		provider.register(AnimationType.JUMP, state.getTilesetManager().getCharactersTileset().KING_JUMP_ANIMATION);
		provider.register(AnimationType.HIT, state.getTilesetManager().getCharactersTileset().KING_HIT_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.SLASH, state.getTilesetManager().getCharactersTileset().KING_SLASH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.PUNCH, state.getTilesetManager().getCharactersTileset().KING_PUNCH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(AnimationType.RUN, state.getTilesetManager().getCharactersTileset().KING_RUN_ANIMATION);
		provider.register(AnimationType.CLIMB, state.getTilesetManager().getCharactersTileset().KING_CLIMB_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(AnimationType.IDLE));
		setHitBox(13, 20);
	}

}
