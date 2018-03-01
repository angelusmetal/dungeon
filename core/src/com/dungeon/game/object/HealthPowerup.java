package com.dungeon.game.object;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.GameState;

public class HealthPowerup extends Entity<HealthPowerup.AnimationType> {

	public static Quaternion HEALTH_LIGHT = new Quaternion(1, 0.1f, 0.2f, 1);

	public HealthPowerup(GameState state, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
		setCurrentAnimation(new GameAnimation<>(AnimationType.IDLE, state.getTilesetManager().getPowerupsTileset().HEALTH_ANIMATION, state.getStateTime()));
		lightRadius = 48;
		lightColor = HEALTH_LIGHT;
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
		return false;
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity<?> entity) {
		if (entity instanceof PlayerCharacter) {
			PlayerCharacter character = (PlayerCharacter) entity;
			character.heal(25);
			expired = true;
			return true;
		} else {
			return false;
		}
	}

}
