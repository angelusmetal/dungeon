package com.dungeon.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dungeon.engine.resource.Resources;

import java.util.LinkedList;

public class InitializationAdapter implements ApplicationListener {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;
	private final LinkedList<InitTask> tasks = new LinkedList<>();
	private InitTask currentTask;
	private int totalTasks = 0;
	private int completedTasks = 0;

	private static class InitTask {
		String label;
		Runnable runnable;

		public InitTask(String label, Runnable runnable) {
			this.label = label;
			this.runnable = runnable;
		}
	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		font = Resources.fonts.get(Resources.DEFAULT_FONT);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		InitTask nextTask = tasks.peekFirst();

		float width = Gdx.graphics.getWidth() * 0.6f;
		float height = Gdx.graphics.getHeight() * 0.1f;
		String message = nextTask != null ? nextTask.label : "";
		GlyphLayout layout = new GlyphLayout(font, message);

		Gdx.gl.glClearColor(0f, 0.08f, 0.15f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.valueOf("005a81ff"));
		shapeRenderer.rect((Gdx.graphics.getWidth() - width) / 2f, height, width, height);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.valueOf("005a81ff"));
		shapeRenderer.rect((Gdx.graphics.getWidth() - width) / 2f + 4, height + 4, width * ((float) completedTasks / totalTasks) - 8, height - 8);
		shapeRenderer.end();

		batch.begin();
		batch.setColor(Color.BLUE);
		font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2f, height * 1.5f);
		batch.end();

		if (currentTask != null) {
			currentTask.runnable.run();
			completedTasks++;
		}
		currentTask = tasks.pollFirst();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
	}

	public void addInitTask(String label, Runnable runnable) {
		tasks.add(new InitTask(label, runnable));
		totalTasks++;
	}

}
