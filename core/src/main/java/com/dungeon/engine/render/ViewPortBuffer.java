package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
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
	private Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
	private Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;

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

	public void setFilters(Texture.TextureFilter minFilter, Texture.TextureFilter magFilter) {
		this.minFilter = minFilter;
		this.magFilter = magFilter;
	}

	public void reset() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = new FrameBuffer(format, viewPort.width, viewPort.height, false);
		textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
		textureRegion.flip(false, true);
		textureRegion.getTexture().setFilter(minFilter, magFilter);
		projectToZero();
	}

	public void projectToViewPort() {
		batch.getProjectionMatrix().setToOrtho2D(viewPort.cameraX, viewPort.cameraY, viewPort.width, viewPort.height);
	}

	public void projectToZero() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.cameraWidth, viewPort.cameraHeight);
	}

	public void projectToZeroFull() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width, viewPort.height);
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
		batch.draw(textureRegion, 0, 0, viewPort.cameraWidth, viewPort.cameraHeight);
	}

	public void drawScaled(SpriteBatch batch) {
		// Adjust the texture region size to the visible part (but never exceed the buffer size)
		textureRegion.setRegionWidth(Math.min(viewPort.cameraWidth, viewPort.width));
		textureRegion.setRegionHeight(Math.min(viewPort.cameraHeight, viewPort.height));
		batch.draw(textureRegion, viewPort.posX, viewPort.posY, viewPort.width, viewPort.height);
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

	public FrameBuffer getFrameBuffer() {
		return frameBuffer;
	}
}
