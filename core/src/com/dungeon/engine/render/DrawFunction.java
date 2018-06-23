package com.dungeon.engine.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

import java.util.function.Supplier;

public interface DrawFunction {
	void draw(ViewPort viewPort, SpriteBatch batch, Entity entity);

	static Supplier<DrawFunction> regular() {
		return () -> (viewport, batch, entity) ->
				viewport.draw(batch, entity.getFrame(), entity.getPos().x, entity.getPos().y + entity.getZPos(), entity.invertX() ? -1 : 1, entity.getDrawOffset());
	}

	static Supplier<DrawFunction> rotateFixed(Texture texture, float speed) {
		return () -> (vp, b, e) -> vp.draw(b, texture, e.getPos().x, e.getPos().y + e.getZPos(), texture.getWidth(), GameState.time() * speed);
	}

	static Supplier<DrawFunction> rotateRandom(Texture texture, float speed) {
		return () -> {
			float actualSpeed = Rand.between(-speed, speed);
			return (vp, b, e) -> vp.draw(b, texture, e.getPos().x, e.getPos().y + e.getZPos(), texture.getWidth(), GameState.time() * actualSpeed);
		};
	}

	static Supplier<DrawFunction> rotateVector(Vector2 rotateVector) {
		return () -> (vp, b, e) -> vp.drawRotated(b, e.getFrame(), e.getPos().x, e.getPos().y + e.getZPos(), rotateVector.angle() + 90, true);
	}

}
