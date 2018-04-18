package com.dungeon.engine.viewport;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ViewPort {
	public final int width;
	public final int height;
	public float scale = 4;
	public int xOffset = 0;
	public int yOffset = 0;

	public ViewPort(int width, int height, int xOffset, int yOffset, float scale) {
		this.width = width;
		this.height = height;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.scale = scale;
	}

	public void draw(SpriteBatch batch, TextureRegion textureRegion, float x, float y, float invertX, Vector2 drawOffset) {
		batch.draw(
				textureRegion,
				(x - xOffset - drawOffset.x * invertX),
				(y - yOffset - drawOffset.y),
				textureRegion.getRegionWidth() * invertX,
				textureRegion.getRegionHeight());
	}

	public void draw(SpriteBatch batch, TextureRegion textureRegion, float x, float y, float width, float height) {
		batch.draw(
				textureRegion,
				(x - xOffset),
				(y - yOffset),
				width,
				height);
	}

	public void draw(SpriteBatch batch, Texture texture, float x, float y, float diameter2, float rotation) {
		float diameter = diameter2;
		float radius = diameter / 2f;
		batch.draw(
				texture,
				(x - xOffset) - radius,
				(y - yOffset) - radius,
				radius,
				radius,
				diameter,
				diameter,
				1,
				1,
				rotation,
				0,
				0,
				texture.getWidth(),
				texture.getHeight(),
				false,
				false);
	}

	@Override
	public String toString() {
		return "ViewPort{" +
				"width=" + width +
				", height=" + height +
				", scale=" + scale +
				", xOffset=" + xOffset +
				", yOffset=" + yOffset +
				'}';
	}
}
