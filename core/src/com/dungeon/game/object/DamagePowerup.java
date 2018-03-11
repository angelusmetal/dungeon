package com.dungeon.game.object;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class DamagePowerup extends Entity<DamagePowerup.AnimationType> {

	public DamagePowerup(GameState state, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
		setCurrentAnimation(new GameAnimation<>(AnimationType.IDLE, state.getTilesetManager().getPowerupsTileset().HEALTH_ANIMATION, state.getStateTime()));
	}

	public enum AnimationType {
		IDLE;
	}

	@Override
	public boolean isExpired(float time) {
		return expired;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
