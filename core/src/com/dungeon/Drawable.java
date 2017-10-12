package com.dungeon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.viewport.ViewPort;

public interface Drawable {

	TextureRegion getFrame(float stateTime);

	Vector2 getPos();
	Vector2 getDrawOffset();

	default void draw(SpriteBatch batch, ViewPort viewPort, float stateTime) {
		TextureRegion characterFrame = getFrame(stateTime);
		batch.draw(characterFrame, (getPos().x - viewPort.xOffset - getDrawOffset().x) * viewPort.scale, (getPos().y - viewPort.yOffset - getDrawOffset().y) * viewPort.scale, characterFrame.getRegionWidth() * viewPort.scale, characterFrame.getRegionHeight() * viewPort.scale);
	}

}
