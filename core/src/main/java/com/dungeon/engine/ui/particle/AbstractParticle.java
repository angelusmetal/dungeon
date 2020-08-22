package com.dungeon.engine.ui.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Material;

public abstract class AbstractParticle implements Particle {
	protected final Vector2 origin = new Vector2();
	protected final Animation<Material> animation;
	protected final float startTime;
	protected final float duration;
	private final Color color;

	protected AbstractParticle(Animation<Material> animation, float duration) {
		this.animation = animation;
		this.startTime = Engine.time();
		this.duration = duration;
		this.color = Color.WHITE.cpy();
	}

	@Override
	public void drawAndUpdate(SpriteBatch batch) {
		move();
		Sprite frame = animation.getKeyFrame(Engine.time()).getDiffuse();
		frame.setColor(color);
		frame.setPosition(origin.x - frame.getWidth() / 2f, origin.y - frame.getHeight() / 2f);
		frame.draw(batch);
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
