package com.dungeon.game.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.PowerupsTileset;

public class HealthPowerup extends Entity {

	public static class Factory implements EntityFactory.EntityTypeFactory {

		final GameState state;
		final Animation<TextureRegion> animation;
		final Light light;

		public Factory(GameState state) {
			this.state = state;
			light = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::oscillating, Light::rotateFast);
			animation = ResourceManager.instance().getAnimation(PowerupsTileset.HEALTH, PowerupsTileset::health);
		}

		@Override
		public HealthPowerup build(Vector2 origin) {
			return new HealthPowerup(this, origin);
		}
	}

	private HealthPowerup(Factory factory, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
		setCurrentAnimation(new GameAnimation(factory.animation, factory.state.getStateTime()));
		light = factory.light;
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
	protected boolean onEntityCollision(GameState state, Entity entity) {
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
