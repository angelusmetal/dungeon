package com.dungeon.character;

import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;

public class King extends Character {

	public King(GameState state) {
		AnimationProvider<CharacterAnimationType> provider = new AnimationProvider<>(CharacterAnimationType.class, state);
		provider.register(CharacterAnimationType.IDLE, state.getTilesetManager().getCharactersTileset().KING_IDLE_ANIMATION);
		provider.register(CharacterAnimationType.WALK, state.getTilesetManager().getCharactersTileset().KING_WALK_ANIMATION);
		provider.register(CharacterAnimationType.JUMP, state.getTilesetManager().getCharactersTileset().KING_JUMP_ANIMATION);
		provider.register(CharacterAnimationType.HIT, state.getTilesetManager().getCharactersTileset().KING_HIT_ANIMATION, this::onSelfMovementUpdate);
		provider.register(CharacterAnimationType.SLASH, state.getTilesetManager().getCharactersTileset().KING_SLASH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(CharacterAnimationType.PUNCH, state.getTilesetManager().getCharactersTileset().KING_PUNCH_ANIMATION, this::onSelfMovementUpdate);
		provider.register(CharacterAnimationType.RUN, state.getTilesetManager().getCharactersTileset().KING_RUN_ANIMATION);
		provider.register(CharacterAnimationType.CLIMB, state.getTilesetManager().getCharactersTileset().KING_CLIMB_ANIMATION);
		setAnimationProvider(provider);
		setCurrentAnimation(provider.get(CharacterAnimationType.IDLE));
	}

}
