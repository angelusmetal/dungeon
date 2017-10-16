package com.dungeon.character;

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

	public void fire(GameState state) {
		Projectile projectile = new Projectile(state, 10, state.getStateTime());
		projectile.moveTo(getPos());
		projectile.setSelfMovement(getSelfMovement());
		float len = projectile.getSelfMovement().len();
		projectile.getSelfMovement().scl(5 / len);
		state.addEntity(projectile);
		setCurrentAnimation(animationProvider.get(AnimationType.HIT));
	}

}
