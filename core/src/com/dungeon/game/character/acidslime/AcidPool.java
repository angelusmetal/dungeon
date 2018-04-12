package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

class AcidPool extends Entity {

	private static final Vector2 BOUNDING_BOX = new Vector2(22, 12);
	private static final float DPS = 5f;
	private static final float TTL = 5f;

	private final AcidSlimeFactory factory;
	private final float expirationTime;

	public AcidPool(AcidSlimeFactory factory, GameState state, Vector2 origin) {
		super(new Body(origin, BOUNDING_BOX));
		this.factory = factory;
		getPos().add(0, -8);
		setCurrentAnimation(new GameAnimation(factory.poolFloodAnimation, state.getStateTime()));
		expirationTime = state.getStateTime() + TTL;
		light = factory.poolLight;
		drawContext = factory.drawContext;
	}

	@Override
	public void think(GameState state) {
		if (state.getStateTime() > expirationTime - 0.5f && getCurrentAnimation().getAnimation() != factory.poolDryAnimation) {
			setCurrentAnimation(new GameAnimation(factory.poolDryAnimation, state.getStateTime()));
		}
	}

	@Override
	public boolean isExpired(float time) {
		return time > expirationTime;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public float getZIndex() {
		return -1;
	}

	@Override
	protected boolean onEntityCollision(GameState state, Entity entity) {
		if (entity instanceof PlayerCharacter) {
			entity.hit(state, DPS * state.getFrameTime());
			return true;
		} else {
			return false;
		}
	}
}
