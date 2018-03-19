package com.dungeon.game.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class HealthPowerup extends Entity<HealthPowerup.AnimationType> {

	static private Light HEALTH_LIGHT = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::oscillating, Light::rotateFast);

	public enum AnimationType {
		IDLE;
	}

	public static class Factory implements EntityFactory.EntityTypeFactory {

		private GameState state;

		public Factory(GameState state) {
			this.state = state;
		}

		@Override
		public Entity<?> build(Vector2 origin) {
			HealthPowerup entity = new HealthPowerup(origin);
			entity.setCurrentAnimation(new GameAnimation<>(AnimationType.IDLE, state.getTilesetManager().getPowerupsTileset().HEALTH_ANIMATION, state.getStateTime()));
			entity.light = HEALTH_LIGHT;
			return entity;
		}
	}

	private HealthPowerup(Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
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
