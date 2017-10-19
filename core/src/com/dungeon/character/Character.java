package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.movement.Movable;
import com.dungeon.projectile.BaseProjectile;
import com.dungeon.viewport.ViewPort;

public class Character extends Entity<Character.AnimationType> implements Movable, Drawable {

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
		if (!expired) {
			BaseProjectile projectile = createProjectile(state);
			projectile.moveTo(getPos());
			// Extra offset to make projectiles appear in the character's hands
			//projectile.getPos().y -= 8;
			aim.clamp(5,5);
			projectile.getPos().mulAdd(aim, 2);
			projectile.setSelfMovement(aim);
			state.addEntity(projectile);
			setCurrentAnimation(animationProvider.get(AnimationType.HIT));
		}
	}

	protected BaseProjectile createProjectile(GameState state) {
		return new King.Projectile(state, 10, state.getStateTime(), dmg);
	}

	@Override
	public void draw(GameState state, SpriteBatch batch, ViewPort viewPort) {
		super.draw(state, batch, viewPort);
		// Draw health bar
		TextureRegion frame = getFrame(state.getStateTime());
		batch.draw(state.getTilesetManager().getHudTileset().HEALTH_BAR, (getPos().x - viewPort.xOffset - getHitBox().x / 2) * viewPort.scale, (getPos().y - viewPort.yOffset + getHitBox().y / 2) * viewPort.scale, getHitBox().x * viewPort.scale * health/maxHealth, 2 * viewPort.scale);
	}

}
