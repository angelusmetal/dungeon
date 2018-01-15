package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.GameState;

public abstract class Character extends Entity<Character.AnimationType> implements Drawable {

	public enum AnimationType {
		IDLE, WALK, JUMP, HIT, SLASH, PUNCH, RUN, CLIMB;
	}

	protected AnimationProvider<AnimationType> animationProvider;
	private Vector2 aim = new Vector2(1, 0);
	protected int dmg = 10;

	public Character(Vector2 pos) {
		super(pos);
	}

	public void setAnimationProvider(AnimationProvider<AnimationType> animationProvider) {
		this.animationProvider = animationProvider;
	}

	@Override
	protected void onSelfMovementUpdate() {
		if (getLinearVelocity().x != 0) {
			setInvertX(getLinearVelocity().x < 0);
		}
		if (getLinearVelocity().x == 0 && getLinearVelocity().y == 0) {
			if (AnimationType.IDLE != getCurrentAnimation().getId()) {
				setCurrentAnimation(animationProvider.get(AnimationType.IDLE));
			}
		} else {
			if (AnimationType.WALK != getCurrentAnimation().getId()) {
				setCurrentAnimation(animationProvider.get(AnimationType.WALK));
			}
		}
		if (getLinearVelocity().len() > 0.5) {
			aim.set(getLinearVelocity());
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

	public void fire(GameState state) {
		if (!expired) {
			aim.clamp(5,5);
			Projectile projectile = createProjectile(state, getPos().cpy().mulAdd(aim, 2), aim.cpy().scl(100_000_000));
			if (projectile != null) {
				// Extra offset to make projectiles appear in the character's hands
				//projectile.getPos().y -= 8;
				state.addEntity(projectile);
				setCurrentAnimation(animationProvider.get(AnimationType.HIT));
			}
		}
	}

	protected Projectile createProjectile(GameState state, Vector2 pos, Vector2 linearVelocity) {
		return null;
	}

	@Override
	public void draw(GameState state, SpriteBatch batch, ViewPort viewPort) {
		super.draw(state, batch, viewPort);
		// Draw health bar
		batch.draw(state.getTilesetManager().getHudTileset().HEALTH_BAR, (getPos().x - viewPort.xOffset - getHitBox().x / 2) * viewPort.scale, (getPos().y - viewPort.yOffset + getHitBox().y / 2) * viewPort.scale, getHitBox().x * viewPort.scale * health/maxHealth, 2 * viewPort.scale);
	}

}
