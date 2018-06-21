package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.game.state.GameState;

public class AcidPool extends Entity {

	private final AcidSlimeFactory factory;

	public AcidPool(Vector2 origin, AcidSlimeFactory factory) {
		super(origin, factory.pool);
		this.factory = factory;
		getPos().add(0, -8);
	}

	@Override
	public void think() {
		if (GameState.time() > expirationTime - 0.5f && getCurrentAnimation().getAnimation() != factory.poolDryAnimation) {
			setCurrentAnimation(factory.poolDryAnimation);
		}
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof PlayerEntity) {
			entity.hit(factory.poolDamage * GameState.frameTime());
			return true;
		} else {
			return false;
		}
	}
}
