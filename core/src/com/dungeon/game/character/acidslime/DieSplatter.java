package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

class DieSplatter extends Entity {

	private static final Vector2 BOUNDING_BOX = new Vector2(22, 12);
	private final float expirationTime;

	public DieSplatter(AcidSlimeFactory factory, GameState state, Vector2 origin) {
		super(new Body(origin, BOUNDING_BOX));
		setCurrentAnimation(new GameAnimation(factory.dieAnimation, state.getStateTime()));
		expirationTime = state.getStateTime() + getCurrentAnimation().getDuration();
		light = factory.characterLight;
		drawContext = factory.drawContext;
	}

	@Override
	public boolean isExpired(float time) {
		return time > expirationTime;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

}
