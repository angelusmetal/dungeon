package com.dungeon.game.object.torch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class Torch extends Entity {

	private static final Vector2 BOUNDING_BOX = new Vector2(10, 10);

	public static class Factory implements EntityFactory.EntityTypeFactory {

		final GameState state;
		final Animation<TextureRegion> animation;
		final Light light;

		public Factory(GameState state) {
			this.state = state;
			light = new Light(80, new Color(1, 0.7f, 0.2f, 1), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
			animation = ResourceManager.instance().getAnimation(TorchSheet.IDLE, TorchSheet::idle);
		}

		@Override
		public Torch build(Vector2 origin) {
			return new Torch(this, origin);
		}
	}

	public Torch(Factory factory, Vector2 position) {
		super(new Body(position, BOUNDING_BOX));
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

}
