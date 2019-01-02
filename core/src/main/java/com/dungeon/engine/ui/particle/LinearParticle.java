package com.dungeon.engine.ui.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;

public class LinearParticle extends AbstractParticle implements Particle {
	private final Vector2 speed;

	public LinearParticle(Vector2 origin, Vector2 speed, Animation<TextureRegion> animation, float duration) {
		super(animation, duration);
		this.origin.set(origin);
		this.speed = speed;
	}

	@Override
	protected void move() {
		origin.add(speed.x * Engine.frameTime(), speed.y * Engine.frameTime());
	}

}
