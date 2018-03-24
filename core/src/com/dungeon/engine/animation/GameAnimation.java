package com.dungeon.engine.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GameAnimation {
	private final Animation<TextureRegion> animation;
	private final float start;
	private final Vector2 drawOffset;

	public GameAnimation(Animation<TextureRegion> animation, float animationStart) {
		this.animation = animation;
		this.start = animationStart;
		TextureRegion firstFrame = getKeyFrame(animationStart);
		this.drawOffset = new Vector2(firstFrame.getRegionWidth() / 2, firstFrame.getRegionHeight() / 2);
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	public Vector2 getDrawOffset() {
		return drawOffset;
	}

	public TextureRegion getKeyFrame(float stateTime) {
		float time = stateTime - start;
		return animation.getKeyFrame(time);
	}

	public float getDuration() {
		return animation.getAnimationDuration();
	}
}
