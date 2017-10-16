package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.Dungeon;
import com.dungeon.animation.GameAnimation;
import com.dungeon.tileset.CharactersTileset32;

public class King extends Character {

	public King(CharactersTileset32 charactersTileset32, Dungeon dungeon) {
		super(new CharacterAnimationProvider() {
			@Override
			public GameAnimation<TextureRegion> getIdle() {
				return new GameAnimation<>("idle", charactersTileset32.KING_IDLE_ANIMATION, dungeon.stateTime);
			}

			@Override
			public GameAnimation<TextureRegion> getWalk() {
				return new GameAnimation<>("walk", charactersTileset32.KING_WALK_ANIMATION, dungeon.stateTime);
			}

			@Override
			public GameAnimation<TextureRegion> getJump() {
				return new GameAnimation<>("jump", charactersTileset32.KING_JUMP_ANIMATION, dungeon.stateTime);
			}

			@Override
			public GameAnimation<TextureRegion> getHit(Runnable endTrigger) {
				return new GameAnimation<>("hit", charactersTileset32.KING_HIT_ANIMATION, dungeon.stateTime, endTrigger);
			}

			@Override
			public GameAnimation<TextureRegion> getSlash() {
				return new GameAnimation<>("slash", charactersTileset32.KING_SLASH_ANIMATION, dungeon.stateTime);
			}

			@Override
			public GameAnimation<TextureRegion> getPunch() {
				return new GameAnimation<>("punch", charactersTileset32.KING_PUNCH_ANIMATION, dungeon.stateTime);
			}

			@Override
			public GameAnimation<TextureRegion> getRun() {
				return new GameAnimation<>("run", charactersTileset32.KING_RUN_ANIMATION, dungeon.stateTime);
			}

			@Override
			public GameAnimation<TextureRegion> getClimb() {
				return new GameAnimation<>("climb", charactersTileset32.KING_CLIMB_ANIMATION, dungeon.stateTime);
			}
		});
	}
}
