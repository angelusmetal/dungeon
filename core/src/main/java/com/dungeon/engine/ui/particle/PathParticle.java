package com.dungeon.engine.ui.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Material;

public class PathParticle extends AbstractParticle implements Particle {
	private final Bezier<Vector2> path;

	public PathParticle(Bezier<Vector2> path, Animation<Material> animation, float duration) {
		super(animation, duration);
		this.path = path;
	}

	@Override
	protected void move() {
		path.valueAt(origin, (Engine.time() - startTime) / duration);
	}

}
