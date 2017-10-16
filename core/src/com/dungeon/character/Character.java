package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.Dungeon;
import com.dungeon.GameState;
import com.dungeon.animation.GameAnimation;
import com.dungeon.level.Level;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public class Character extends Entity implements Movable, Drawable {
	private CharacterAnimationProvider animationProvider;

	public Character(CharacterAnimationProvider animationProvider) {
		super(animationProvider.getIdle());
		this.animationProvider = animationProvider;
	}

	public Character(CharacterAnimationProvider animationProvider, Vector2 drawOffset) {
		super(animationProvider.getIdle());
		this.animationProvider = animationProvider;
		this.drawOffset.set(drawOffset);
	}

	@Override
	protected void onSelfMovementUpdate() {
		if (getSelfMovement().x != 0) {
			setInvertX(getSelfMovement().x < 0);
		}
		if (getSelfMovement().x == 0 && getSelfMovement().y == 0) {
			if (!"idle".equals(getCurrentAnimation().getId())) {
				setCurrentAnimation(animationProvider.getIdle());
			}
		} else {
			if (!"walk".equals(getCurrentAnimation().getId())) {
				setCurrentAnimation(animationProvider.getWalk());
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
		setCurrentAnimation(animationProvider.getHit(this::onSelfMovementUpdate));
	}
}
