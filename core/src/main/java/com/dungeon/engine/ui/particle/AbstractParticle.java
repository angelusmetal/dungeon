package com.dungeon.engine.ui.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;

public abstract class AbstractParticle implements Particle {
	protected final Vector2 origin = new Vector2();
	protected final Animation<TextureRegion> animation;
	protected final float startTime;
	protected final float duration;
	private final Color color;

	protected AbstractParticle(Animation<TextureRegion> animation, float duration) {
		this.animation = animation;
		this.startTime = Engine.time();
		this.duration = duration;
		this.color = Color.WHITE.cpy();
	}

	@Override
	public void drawAndUpdate(SpriteBatch batch) {
		move();
		TextureRegion frame = animation.getKeyFrame(Engine.time());
		batch.setColor(color);
		batch.draw(frame, origin.x - frame.getRegionWidth() / 2f, origin.y - frame.getRegionHeight() / 2f);
		update();
	}

	@Override
	public boolean isExpired() {
		return startTime + duration <= Engine.time();
	}

	@Override
	public void expire() {}

	public void update() {}

	@Override
	public Color getColor() {
		return color;
	}

	protected abstract void move();

}
