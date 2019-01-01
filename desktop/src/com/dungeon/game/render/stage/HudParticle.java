package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;

public class HudParticle {
	private final Vector2 origin = new Vector2();
	private final Animation<TextureRegion> animation;
	private final Bezier<Vector2> path;
	private final float startTime;
	private final float duration;
	private final Runnable action;

	public HudParticle(Animation<TextureRegion> animation, Bezier<Vector2> path, float duration, Runnable action) {
		this.animation = animation;
		this.path = path;
		this.startTime = Engine.time();
		this.duration = duration;
		this.action = action;
	}

	public void drawAndUpdate(SpriteBatch batch) {
		path.valueAt(origin, (Engine.time() - startTime) / duration);
		TextureRegion frame = animation.getKeyFrame(Engine.time());
		batch.draw(frame, origin.x - frame.getRegionWidth() / 2f, origin.y - frame.getRegionHeight() / 2f);
	}

	public boolean isExpired() {
		return startTime + duration <= Engine.time();
	}

	public void runAction() {
		action.run();
	}
}
