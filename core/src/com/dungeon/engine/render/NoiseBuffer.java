package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.moandjiezana.toml.Toml;

public class NoiseBuffer {
	private final int width;
	private final int height;
	private final float grit;
	private final FrameBuffer frameBuffer;
	private final TextureRegion textureRegion;
	private final SpriteBatch batch;

	public NoiseBuffer(Toml configuration) {
		this.width = configuration.getLong("rendering.noiseWidth", 128L).intValue();
		this.height = configuration.getLong("rendering.noiseHeight", 128L).intValue();
		this.grit = Util.clamp(configuration.getDouble("rendering.noiseGrit", 0.3d).floatValue());
		this.batch = new SpriteBatch();
		frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, width, height, false);
		textureRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
		textureRegion.flip(false, true);
		textureRegion.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	public void renderNoise() {
		Texture texture = ResourceManager.instance().getTexture("fill.png");
		frameBuffer.begin();
		batch.begin();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				float r = Rand.between(1 - grit, 1f);
				batch.setColor(r, r, r, 1);
				batch.draw(texture, x, y, 1, 1);
			}
		}
		batch.setColor(Color.WHITE);
		batch.end();
		frameBuffer.end();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void dispose() {
		if (frameBuffer != null) {
			frameBuffer.dispose();
		}
	}

	public void draw(SpriteBatch batch, int bufferWidth, int buffereHeight) {
		int xStart = Rand.between(-width, 0);
		int yStart = Rand.between(-height, 0);
		batch.setColor(1f, 1f, 1f, 0.1f);
		for (int x = xStart; x < bufferWidth; x += width) {
			for (int y = yStart; y < buffereHeight; y += height) {
				batch.draw(textureRegion, x, y, width, height);
			}
		}
		batch.setColor(Color.WHITE);
	}

}
