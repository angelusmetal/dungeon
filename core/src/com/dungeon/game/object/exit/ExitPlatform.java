package com.dungeon.game.object.exit;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class ExitPlatform extends Entity {

	private static final Vector2 BOUNDING_BOX = new Vector2(32, 32);
	boolean exited = false;

	ExitPlatform(ExitPlatformFactory factory, Vector2 origin) {
		super(new Body(origin, BOUNDING_BOX));
		setCurrentAnimation(new GameAnimation(factory.animation, 0));
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (!exited && entity instanceof PlayerCharacter) {
			exited = true;
			state.exitLevel();
		}
		return true;
	}

	@Override
	public float getZIndex() {
		return -1;
	}
}
