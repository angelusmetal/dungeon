package com.dungeon.character;

import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.movement.Movable;

public class Character extends Entity<CharacterAnimationType> implements Movable, Drawable {

	private AnimationProvider<CharacterAnimationType> animationProvider;

	public void setAnimationProvider(AnimationProvider<CharacterAnimationType> animationProvider) {
		this.animationProvider = animationProvider;
	}

	@Override
	protected void onSelfMovementUpdate() {
		if (getSelfMovement().x != 0) {
			setInvertX(getSelfMovement().x < 0);
		}
		if (getSelfMovement().x == 0 && getSelfMovement().y == 0) {
			if (CharacterAnimationType.IDLE != getCurrentAnimation().getId()) {
				setCurrentAnimation(animationProvider.get(CharacterAnimationType.IDLE));
			}
		} else {
			if (CharacterAnimationType.WALK != getCurrentAnimation().getId()) {
				setCurrentAnimation(animationProvider.get(CharacterAnimationType.WALK));
			}
		}
	}

	public void fire(GameState state) {
		Projectile projectile = new Projectile(state.getTilesetManager().getProjectileTileset().PROJECTILE_ANIMATION, 10, state.getStateTime());
		projectile.moveTo(getPos());
		projectile.setSelfMovement(getSelfMovement());
		float len = projectile.getSelfMovement().len();
		projectile.getSelfMovement().scl(5 / len);
		state.addProjectile(projectile);
		setCurrentAnimation(animationProvider.get(CharacterAnimationType.HIT));
	}
}
