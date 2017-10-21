package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.GameState;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.viewport.ViewPort;

public abstract class Character extends Entity<Character.AnimationType> implements Movable, Drawable {

	public enum AnimationType {
		IDLE, WALK, JUMP, HIT, SLASH, PUNCH, RUN, CLIMB;
	}

	protected AnimationProvider<AnimationType> animationProvider;
	private Vector2 aim = new Vector2(1, 0);
	protected int dmg = 10;

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
		if (getSelfMovement().len() > 0.5) {
			aim.set(getSelfMovement());
		}
	}

	@Override
	public boolean isExpired(float time) {
		return expired;
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
			moveTo(oldPos);
		} else if (getMovement().x > 0 && getPos().x > state.getViewPort().xOffset + (state.getViewPort().width / state.getViewPort().scale)) {
			moveTo(oldPos);
		}
		if (getMovement().y < 0 && getPos().y < state.getViewPort().yOffset) {
			moveTo(oldPos);
		} else if (getMovement().y > 0 && getPos().y > state.getViewPort().yOffset + (state.getViewPort().height / state.getViewPort().scale)) {
			moveTo(oldPos);
		}
	}

	public void fire(GameState state) {
		if (!expired) {
			Projectile projectile = createProjectile(state);
			if (projectile != null) {
				aim.clamp(5,5);
				projectile.moveTo(getPos().cpy().mulAdd(aim, 2));
				projectile.setSelfMovement(aim);
				// Extra offset to make projectiles appear in the character's hands
				//projectile.getPos().y -= 8;
				state.addEntity(projectile);
				setCurrentAnimation(animationProvider.get(AnimationType.HIT));
			}
		}
	}

	protected Projectile createProjectile(GameState state) {
		return null;
	}

	@Override
	public void draw(GameState state, SpriteBatch batch, ViewPort viewPort) {
		super.draw(state, batch, viewPort);
		// Draw health bar
		batch.draw(state.getTilesetManager().getHudTileset().HEALTH_BAR, (getPos().x - viewPort.xOffset - getHitBox().x / 2) * viewPort.scale, (getPos().y - viewPort.yOffset + getHitBox().y / 2) * viewPort.scale, getHitBox().x * viewPort.scale * health/maxHealth, 2 * viewPort.scale);
	}

}
