package com.dungeon.animation;

import com.badlogic.gdx.graphics.g2d.Animation;

public class GameAnimation<T> {
	private final String id;
	private final Animation<T> animation;
	private final float start;
	private final Runnable endTrigger;

	public GameAnimation(String id, Animation<T> animation, float animationStart) {
		this.id = id;
		this.animation = animation;
		this.start = animationStart;
		this.endTrigger = () -> {};
	}

	public GameAnimation(String id, Animation<T> animation, float animationStart, Runnable endTrigger) {
		this.id = id;
		this.animation = animation;
		this.start = animationStart;
		this.endTrigger = endTrigger;
	}

	public String getId() {
		return id;
	}

	public Animation<T> getAnimation() {
		return animation;
	}

	public float getStart() {
		return start;
	}

	public Runnable getEndTrigger() {
		return endTrigger;
	}

	public T getKeyFrame(float stateTime) {
		float time = stateTime - start;
		T keyFrame = animation.getKeyFrame(time);
		if (animation.isAnimationFinished(time)) {
			endTrigger.run();
		}
		return keyFrame;
	}
}
