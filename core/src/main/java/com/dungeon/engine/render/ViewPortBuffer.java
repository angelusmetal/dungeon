package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.viewport.ViewPort;

import java.util.function.Consumer;

public class ViewPortBuffer implements Disposable {
	private final Pixmap.Format format;
	private FrameBuffer frameBuffer;
	private TextureRegion textureRegion;
	private ViewPort viewPort;
	private SpriteBatch batch;
	private int renderCalls;

	public ViewPortBuffer(ViewPort viewPort) {
		this.viewPort = viewPort;
		this.batch = new SpriteBatch();
		this.format = Pixmap.Format.RGB888;
	}

	public ViewPortBuffer(ViewPort viewPort, Pixmap.Format format) {
		this.viewPort = viewPort;
		this.batch = new SpriteBatch();
		this.format = format;
	}

	public void reset() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = new FrameBuffer(format, viewPort.cameraWidth, viewPort.cameraHeight, false);
		textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
		textureRegion.flip(false, true);
		textureRegion.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.cameraWidth, viewPort.cameraHeight);
	}

	@Override
	public void dispose() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
	}

	public void render(Consumer<SpriteBatch> consumer) {
		frameBuffer.begin();
		batch.begin();
		consumer.accept(batch);
		renderCalls += batch.renderCalls;
		batch.end();
		frameBuffer.end();
	}

	public void draw(SpriteBatch batch) {
		batch.draw(textureRegion, 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
	}

	public void drawScaled(SpriteBatch batch) {
		batch.draw(textureRegion, viewPort.posX, viewPort.posY, frameBuffer.getWidth() * viewPort.getScale(), frameBuffer.getHeight() * viewPort.getScale());
	}

	public int getLastRenderCalls() {
		return renderCalls;
	}

	public void resetLastRenderCalls() {
		renderCalls = 0;
	}

	public Texture getTexture() {
		return textureRegion.getTexture();
	}

}
