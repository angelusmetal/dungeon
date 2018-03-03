package com.dungeon.game.object;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.game.GameState;

public class Torch extends Entity<Torch.AnimationType> {

	static private Light TORCH_LIGHT = new Light(80, new Quaternion(1, 0.7f, 0.2f, 1), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

	public Torch(GameState state, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
		setCurrentAnimation(new GameAnimation<>(AnimationType.IDLE, state.getTilesetManager().getTorchTileset().TORCH_ANIMATION, state.getStateTime()));
		light = TORCH_LIGHT;
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
