package com.dungeon.engine.render.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Util;
import com.dungeon.game.state.GameState;

public class FadeEffect implements RenderEffect {

	private static final int DEFAULT_FADE_DURATION = 1;
	private final Color start;
	private final Color end;
	private final Color currentBlend;
	private final float startTime;
	private final float duration;
	private final Runnable endAction;
	private SpriteBatch batch;
	private Texture fill;

	public FadeEffect(Color start, Color end, float startTime, float duration, Runnable endAction) {
		batch = new SpriteBatch();
		this.start = start;
		this.end = end;
		this.currentBlend = new Color(start.r, start.g, start.b, start.a);
		this.startTime = startTime;
		this.duration = duration;
		this.endAction = endAction;
		this.fill = ResourceManager.instance().getTexture("fill.png");
	}

	@Override
	public void render() {
		float ratio = Util.clamp((GameState.time() - startTime) / duration, 0f, 1f);
		float mix = 1 - ratio;
		currentBlend.r = start.r * mix + end.r * ratio;
		currentBlend.g = start.g * mix + end.g * ratio;
		currentBlend.b = start.b * mix + end.b * ratio;
		currentBlend.a = start.a * mix + end.a * ratio;
		batch.begin();
		batch.setColor(currentBlend);
		batch.draw(fill, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
	}

	@Override
	public boolean isExpired() {
		return GameState.time() > startTime + duration;
	}

	@Override
	public void dispose() {
		batch.dispose();
		endAction.run();
	}

	public static FadeEffect fadeIn(float startTime) {
		return new FadeEffect(Color.BLACK, Color.CLEAR, startTime, DEFAULT_FADE_DURATION, () -> {});
	}

	public static FadeEffect fadeIn(float startTime, Runnable endAction) {
		return new FadeEffect(Color.BLACK, Color.CLEAR, startTime, DEFAULT_FADE_DURATION, endAction);
	}

	public static FadeEffect fadeOut(float startTime) {
		return new FadeEffect(Color.CLEAR, Color.BLACK, startTime, DEFAULT_FADE_DURATION, () -> {});
	}

	public static FadeEffect fadeOut(float startTime, Runnable endAction) {
		return new FadeEffect(Color.CLEAR, Color.BLACK, startTime, DEFAULT_FADE_DURATION, endAction);
	}

	public static FadeEffect fadeOutDeath(float startTime, Runnable endAction) {
		return new FadeEffect(Color.CLEAR, Color.BLACK, startTime, DEFAULT_FADE_DURATION * 5, endAction);
	}
}
