package com.dungeon.engine.viewport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
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

	public void draw(SpriteBatch batch, TextureRegion textureRegion, float x, float y, float width, float height) {
		batch.draw(
				textureRegion,
				(x - cameraX),
				(y - cameraY),
				width,
				height);
	}

	public void drawEntity(SpriteBatch batch, Entity entity) {
		TextureRegion frame = entity.getFrame();
		batch.draw(
				frame,
				(int) (entity.getOrigin().x - cameraX) - entity.getDrawOffset().x,
				(int) (entity.getOrigin().y - cameraY) - entity.getDrawOffset().y + entity.getZPos(),
				entity.getDrawOffset().x,
				entity.getDrawOffset().y,
				frame.getRegionWidth(),
				frame.getRegionHeight(),
				entity.getDrawScale().x,
				entity.getDrawScale().y,
				entity.getRotation());
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
				e.getOrigin().x - e.getDrawOffset().x < cameraX + cameraWidth &&
				e.getOrigin().x - e.getDrawOffset().x + e.getFrame().getRegionWidth() > cameraX &&
				e.getOrigin().y - e.getDrawOffset().y + e.getZPos() < cameraY + cameraHeight &&
				e.getOrigin().y - e.getDrawOffset().y + e.getZPos() + e.getFrame().getRegionHeight() > cameraY;
	}

	public boolean lightIsInViewPort(Entity e) {
		return
				e.getLight() != null &&
						e.getOrigin().x - e.getLight().diameter < cameraX + cameraWidth &&
						e.getOrigin().x + e.getLight().diameter > cameraX &&
						e.getOrigin().y - e.getLight().diameter < cameraY + cameraHeight &&
						e.getOrigin().y + e.getLight().diameter > cameraY;
	}

	public Vector2 worldToScreen(Vector2 vector2) {
		return new Vector2(vector2.x - cameraX, vector2.y - cameraY);
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
