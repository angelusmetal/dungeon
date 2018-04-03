package com.dungeon.engine.render.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.state.GameState;

public class FadeEffect implements RenderEffect {

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
		System.out.println("New FadeEffect!");
	}

	@Override
	public void render(GameState state) {
		float ratio = Math.min((state.getStateTime() - startTime) / duration, 1f);
		float mix = 1 - ratio;
		currentBlend.r = start.r * mix + end.r * ratio;
		currentBlend.g = start.g * mix + end.g * ratio;
		currentBlend.b = start.b * mix + end.b * ratio;
		currentBlend.a = start.a * mix + end.a * ratio;
		batch.begin();
		batch.setColor(currentBlend);
		batch.draw(fill, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
//		Gdx.gl.glClearColor(
//				start.r * ratio + end.r * mix,
//				start.g * ratio + end.g * mix,
//				start.b * ratio + end.b * mix,
//				start.a * ratio + end.a * mix);
//		Gdx.gl.glClearColor(1, 0, 0, 0f);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		System.out.println("ratio: " + ratio + ", mix: " + mix);
//		System.out.println("alpha: " + (start.a * ratio + end.a * mix));
	}

	@Override
	public boolean isExpired(float time) {
		return time > startTime + duration;
	}

	@Override
	public void dispose() {
		batch.dispose();
		endAction.run();
	}

	public static FadeEffect fadeIn(float startTime) {
		return new FadeEffect(Color.BLACK, Color.CLEAR, startTime, 1, () -> {});
	}

	public static FadeEffect fadeIn(float startTime, Runnable endAction) {
		return new FadeEffect(Color.BLACK, Color.CLEAR, startTime, 1, endAction);
	}

	public static FadeEffect fadeOut(float startTime) {
		return new FadeEffect(Color.CLEAR, Color.BLACK, startTime, 1, () -> {});
	}

	public static FadeEffect fadeOut(float startTime, Runnable endAction) {
		return new FadeEffect(Color.CLEAR, Color.BLACK, startTime, 1, endAction);
	}
}
