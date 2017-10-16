package com.dungeon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.viewport.ViewPort;

public interface Drawable {

	TextureRegion getFrame(float stateTime);
	boolean invertX();

	Vector2 getPos();
	Vector2 getDrawOffset();

	default void draw(SpriteBatch batch, ViewPort viewPort, float stateTime) {
		TextureRegion characterFrame = getFrame(stateTime);
		float invertX = invertX() ? -1 : 1;
		batch.draw(characterFrame, (getPos().x - viewPort.xOffset - getDrawOffset().x * invertX) * viewPort.scale, (getPos().y - viewPort.yOffset - getDrawOffset().y) * viewPort.scale, characterFrame.getRegionWidth() * viewPort.scale * invertX, characterFrame.getRegionHeight() * viewPort.scale);
	}

}
