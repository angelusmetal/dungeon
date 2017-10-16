package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.animation.GameAnimation;

public interface CharacterAnimationProvider {

	GameAnimation<TextureRegion> getIdle();
	GameAnimation<TextureRegion> getWalk();
	GameAnimation<TextureRegion> getJump();
	GameAnimation<TextureRegion> getHit(Runnable endTrigger);
	GameAnimation<TextureRegion> getSlash();
	GameAnimation<TextureRegion> getPunch();
	GameAnimation<TextureRegion> getRun();
	GameAnimation<TextureRegion> getClimb();

}
