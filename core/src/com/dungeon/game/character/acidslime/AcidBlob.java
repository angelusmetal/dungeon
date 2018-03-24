package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.game.state.GameState;

class AcidBlob extends Entity {

	private final AcidSlimeFactory factory;
	private float expirationTime;
	private float z;
	private float zSpeed;
	private boolean collided;
	private Vector2 drawOffset = new Vector2(0, 0);

	public AcidBlob(AcidSlimeFactory factory, GameState state, Vector2 origin) {
		super(new Body(origin, new Vector2(8, 8)));
		this.factory = factory;
		getPos().add(0, -8);
		z = 1;
		zSpeed = (float) Math.random() * 100;
		setCurrentAnimation(new GameAnimation(factory.blobAnimation, state.getStateTime()));
		expirationTime = Float.MAX_VALUE;
		light = factory.poolLight;
		drawContext = new ColorContext(new Color(1, 1, 1, 0.5f));
		setSelfXMovement((float) Math.random() * 100f - 50f);
		setSelfYMovement((float) Math.random() * 20f - 10f);
		speed = 50;
	}

	@Override
	public Vector2 getDrawOffset() {
		drawOffset.y = -z;
		return drawOffset;
	}

	@Override
	public void think(GameState state) {
		if (!collided) {
			z += zSpeed * state.getFrameTime();
			zSpeed -= 70 * state.getFrameTime();
			if (z <= 0) {
				collided = true;
				expirationTime = state.getStateTime() + getCurrentAnimation().getDuration();
				setSelfMovement(Vector2.Zero);
				setCurrentAnimation(new GameAnimation(factory.splatAnimation, state.getStateTime()));
			}
		}
	}

	@Override
	public boolean isExpired(float time) {
		return collided && time > expirationTime;
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
			entity.hit(state, 5 * state.getFrameTime());
			return true;
		} else {
			return false;
		}
	}
}
