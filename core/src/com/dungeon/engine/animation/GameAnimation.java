package com.dungeon.engine.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GameAnimation<A extends Enum<A>> {
	private final A id;
	private final Animation<TextureRegion> animation;
	private final float start;
	private final Runnable endTrigger;
	private final Vector2 drawOffset;

	public GameAnimation(A id, Animation<TextureRegion> animation, float animationStart) {
		this.id = id;
		this.animation = animation;
		this.start = animationStart;
		this.endTrigger = () -> {};
		TextureRegion firstFrame = getKeyFrame(animationStart);
		this.drawOffset = new Vector2(firstFrame.getRegionWidth() / 2, firstFrame.getRegionHeight() / 2);
	}

	public GameAnimation(A id, Animation<TextureRegion> animation, float animationStart, Runnable endTrigger) {
		this.id = id;
		this.animation = animation;
		this.start = animationStart;
		this.endTrigger = endTrigger;
		TextureRegion firstFrame = getKeyFrame(animationStart);
		this.drawOffset = new Vector2(firstFrame.getRegionWidth() / 2, firstFrame.getRegionHeight() / 2);
	}

	public A getId() {
		return id;
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	public float getStart() {
		return start;
	}

	public Runnable getEndTrigger() {
		return endTrigger;
	}

	public Vector2 getDrawOffset() {
		return drawOffset;
	}

	public TextureRegion getKeyFrame(float stateTime) {
		float time = stateTime - start;
		TextureRegion keyFrame = animation.getKeyFrame(time);
		if (animation.isAnimationFinished(time)) {
			endTrigger.run();
		}
		return keyFrame;
	}

	public float getDuration() {
		return animation.getFrameDuration() * animation.getKeyFrames().length;
	}
}
