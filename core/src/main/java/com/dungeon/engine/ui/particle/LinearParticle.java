package com.dungeon.engine.ui.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Material;

public class LinearParticle extends AbstractParticle implements Particle {
	private final Vector2 speed;

	public LinearParticle(Vector2 origin, Vector2 speed, Animation<Material> animation, float duration) {
		super(animation, duration);
		this.origin.set(origin);
		this.speed = speed;
	}

	@Override
	protected void move() {
		origin.add(speed.x * Engine.frameTime(), speed.y * Engine.frameTime());
	}

}
