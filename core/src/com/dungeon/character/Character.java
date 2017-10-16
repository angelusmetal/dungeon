package com.dungeon.character;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.movement.Movable;

public class Character extends Entity<Character.AnimationType> implements Movable, Drawable {

	public enum AnimationType {
		IDLE, WALK, JUMP, HIT, SLASH, PUNCH, RUN, CLIMB;
	}

	private AnimationProvider<AnimationType> animationProvider;

	public void setAnimationProvider(AnimationProvider<AnimationType> animationProvider) {
		this.animationProvider = animationProvider;
	}

	@Override
	protected void onSelfMovementUpdate() {
		if (getSelfMovement().x != 0) {
			setInvertX(getSelfMovement().x < 0);
		}
		if (getSelfMovement().x == 0 && getSelfMovement().y == 0) {
			if (AnimationType.IDLE != getCurrentAnimation().getId()) {
				setCurrentAnimation(animationProvider.get(AnimationType.IDLE));
			}
		} else {
			if (AnimationType.WALK != getCurrentAnimation().getId()) {
				setCurrentAnimation(animationProvider.get(AnimationType.WALK));
			}
		}
	}

	@Override
	public boolean isExpired(float time) {
		return false;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public void move(GameState state) {
		Vector2 oldPos = new Vector2(getPos());
		super.move(state);
		// Also, add collision against the viewport boundaries
		if (getMovement().x < 0 && getPos().x < state.getViewPort().xOffset) {
			getPos().set(oldPos);
		} else if (getMovement().x > 0 && getPos().x > state.getViewPort().xOffset + (state.getViewPort().width / state.getViewPort().scale)) {
			getPos().set(oldPos);
		}
		if (getMovement().y < 0 && getPos().y < state.getViewPort().yOffset) {
			getPos().set(oldPos);
		} else if (getMovement().y > 0 && getPos().y > state.getViewPort().yOffset + (state.getViewPort().height / state.getViewPort().scale)) {
			getPos().set(oldPos);
		}
	}

	public void fire(GameState state) {
		Projectile projectile = new Projectile(state, 10, state.getStateTime());
		projectile.moveTo(getPos());
		// Extra offset to make projectiles appear in the character's hands
		//projectile.getPos().y -= 8;
		projectile.getPos().mulAdd(getSelfMovement(), 11);
		projectile.setSelfMovement(getSelfMovement());
		float len = projectile.getSelfMovement().len();
		projectile.getSelfMovement().scl(5 / len);
		state.addEntity(projectile);
		setCurrentAnimation(animationProvider.get(AnimationType.HIT));
	}

}
