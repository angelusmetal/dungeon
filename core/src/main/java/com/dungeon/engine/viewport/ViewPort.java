package com.dungeon.engine.viewport;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
		cameraWidth = (int) (width / scale);
		cameraHeight = (int) (height / scale);
	}

	public void centerAt(int centerX, int centerY) {
		cameraX = centerX - cameraWidth / 2;
		cameraY = centerY - cameraHeight / 2;
	}

	// TODO this method doesn't belong here
	public void draw(SpriteBatch batch, Texture texture, float x, float y, float diameter, float rotation) {
		float radius = diameter / 2f;
		batch.draw(
				texture,
				x - radius,
				y - radius,
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

	public boolean isInViewPort(Entity e) {
		return
				e.getBody().getBottomLeft().x - e.getDrawOffset().x < cameraX + cameraWidth &&
				e.getBody().getTopRight().x - e.getDrawOffset().x + e.getFrame().getRegionWidth() > cameraX &&
				e.getBody().getBottomLeft().y - e.getDrawOffset().y + e.getZPos() < cameraY + cameraHeight &&
				e.getBody().getTopRight().y - e.getDrawOffset().y + e.getZPos() + e.getFrame().getRegionHeight() > cameraY;
	}

	public boolean lightIsInViewPort(Entity e) {
		return
				e.getLight() != null &&
						e.getOrigin().x - e.getLight().diameter < cameraX + cameraWidth &&
						e.getOrigin().x + e.getLight().diameter > cameraX &&
						e.getOrigin().y - e.getLight().diameter < cameraY + cameraHeight &&
						e.getOrigin().y + e.getLight().diameter > cameraY;
	}

	public boolean flareIsInViewPort(Entity e) {
		return
				e.getFlare() != null &&
						e.getOrigin().x - e.getFlare().diameter < cameraX + cameraWidth &&
						e.getOrigin().x + e.getFlare().diameter > cameraX &&
						e.getOrigin().y - e.getFlare().diameter < cameraY + cameraHeight &&
						e.getOrigin().y + e.getFlare().diameter > cameraY;
	}

	public Vector2 worldToScreen(Vector2 vector2) {
		return new Vector2(vector2.x - cameraX, vector2.y - cameraY);
	}

	public Vector2 screenToWorld(Vector2 vector2) {
		return new Vector2(vector2.x + cameraX, vector2.y + cameraY);
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
