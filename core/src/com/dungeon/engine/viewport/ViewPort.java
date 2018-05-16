package com.dungeon.engine.viewport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;

public class ViewPort {
	public final int posX;
	public final int posY;
	public final int width;
	public final int height;
	public int cameraX = 0;
	public int cameraY = 0;
	public int cameraWidth;
	public int cameraHeight;
	private float scale;

	public ViewPort(int posX, int posY, int width, int height, float scale) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		setScale(scale);
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		this.cameraWidth = (int) (width / scale);
		this.cameraHeight = (int) (height / scale);
	}

	public void draw(SpriteBatch batch, TextureRegion textureRegion, float x, float y, float invertX, Vector2 drawOffset) {
		batch.draw(
				textureRegion,
				(x - cameraX - drawOffset.x * invertX),
				(y - cameraY - drawOffset.y),
				textureRegion.getRegionWidth() * invertX,
				textureRegion.getRegionHeight());
	}

	public void draw(SpriteBatch batch, TextureRegion textureRegion, float x, float y, float width, float height) {
		batch.draw(
				textureRegion,
				(x - cameraX),
				(y - cameraY),
				width,
				height);
	}

	public void draw(SpriteBatch batch, Texture texture, float x, float y, float diameter, float rotation) {
		float radius = diameter / 2f;
		batch.draw(
				texture,
				(x - cameraX) - radius,
				(y - cameraY) - radius,
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

	public void draw(SpriteBatch batch, BitmapFont font, float x, float y, String text, Color color) {
		font.setColor(color);
		font.draw(batch, text, x - cameraX, y - cameraY);
	}

	public boolean isInViewPort(Entity e) {
		return
				e.getPos().x - e.getDrawOffset().x < cameraX + cameraWidth &&
				e.getPos().x + e.getDrawOffset().x + e.getFrame().getRegionWidth() > cameraX &&
				e.getPos().y - e.getDrawOffset().y + e.getZPos() < cameraY + cameraHeight &&
				e.getPos().y + e.getDrawOffset().y + e.getZPos() + e.getFrame().getRegionHeight() > cameraY;
	}

	@Override
	public String toString() {
		return "ViewPort{" +
				"width=" + width +
				", height=" + height +
				", scale=" + scale +
				", cameraX=" + cameraX +
				", cameraY=" + cameraY +
				'}';
	}
}
