package com.dungeon.engine.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.Engine;

public class GameAnimation {
	private final Animation<TextureRegion> animation;
	private final float start;

	public GameAnimation(Animation<TextureRegion> animation) {
		this(animation, Engine.time());
	}

	public GameAnimation(Animation<TextureRegion> animation, float animationStart) {
		this.animation = animation;
		this.start = animationStart;
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	public TextureRegion getKeyFrame(float stateTime) {
		float time = stateTime - start;
		return animation.getKeyFrame(time);
	}

	public float getDuration() {
		return animation.getAnimationDuration();
	}

	public boolean isFinished() {
		return Engine.time() >= start + animation.getAnimationDuration();
	}
}
