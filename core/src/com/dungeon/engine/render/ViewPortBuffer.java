package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.dungeon.engine.viewport.ViewPort;

import java.util.function.Consumer;

public class ViewPortBuffer {
	private FrameBuffer frameBuffer;
	private TextureRegion textureRegion;
	private ViewPort viewPort;
	private SpriteBatch batch;

	public ViewPortBuffer(ViewPort viewPort) {
		this.viewPort = viewPort;
		this.batch = new SpriteBatch();
	}

	public void reset() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
		frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, viewPort.cameraWidth, viewPort.cameraHeight, false);
		textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
		textureRegion.flip(false, true);
		textureRegion.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.cameraWidth, viewPort.cameraHeight);
	}

	public void dispose() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
	}

	public void render(Consumer<SpriteBatch> consumer) {
		frameBuffer.begin();
		batch.begin();
		consumer.accept(batch);
		batch.end();
		frameBuffer.end();
	}

	public void draw(SpriteBatch batch) {
		batch.draw(textureRegion, 0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
	}

	public void drawScaled(SpriteBatch batch) {
		batch.draw(textureRegion, viewPort.posX, viewPort.posY, frameBuffer.getWidth() * viewPort.getScale(), frameBuffer.getHeight() * viewPort.getScale());
	}
}
