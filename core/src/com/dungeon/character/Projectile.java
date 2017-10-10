package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Projectile extends Character {
	private float timeToLive;
	private float startTime;

	public Projectile(Animation<TextureRegion> frames, float timeToLive, float startTime) {
		super(frames);
		this.timeToLive = timeToLive;
		this.startTime = startTime;
	}

	public boolean isDone(float time) {
		return (startTime + timeToLive) < time;
	}
}
