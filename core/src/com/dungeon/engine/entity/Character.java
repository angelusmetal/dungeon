package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.GameState;

public abstract class Character extends Entity<Character.AnimationType> implements Movable, Drawable {

	public enum AnimationType {
		IDLE, WALK, JUMP, HIT, SLASH, PUNCH, RUN, CLIMB;
	}

	protected AnimationProvider<AnimationType> animationProvider;
	private Vector2 aim = new Vector2(1, 0);
	protected int dmg = 10;

	protected CooldownTrigger fireCooldown = new CooldownTrigger(0.5f); // Default is fire every 0.5 seconds

	public Character(Body body) {
		super(body);
	}

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

	public void fire(GameState state) {
		if (!expired) {
			fireCooldown.attempt(state.getStateTime(), () -> {
				aim.clamp(5,5);
				Projectile projectile = createProjectile(state, getPos().cpy().mulAdd(aim, 2));
				if (projectile != null) {
					aim.clamp(projectile.getSpeed(), projectile.getSpeed());
					projectile.setSelfMovement(aim);
					// Extra offset to make projectiles appear in the character's hands
					//projectile.getPos().y -= 8;
					state.addEntity(projectile);
					setCurrentAnimation(animationProvider.get(AnimationType.HIT));
				}
			});
		}
	}

	protected Projectile createProjectile(GameState state, Vector2 pos) {
		return null;
	}

	@Override
	public void draw(GameState state, SpriteBatch batch, ViewPort viewPort) {
		super.draw(state, batch, viewPort);
		// Draw health bar
		batch.draw(state.getTilesetManager().getHudTileset().HEALTH_BAR, (getPos().x - viewPort.xOffset - getBoundingBox().x / 2) * viewPort.scale, (getPos().y - viewPort.yOffset + getBoundingBox().y / 2) * viewPort.scale, getBoundingBox().x * viewPort.scale * health/maxHealth, 2 * viewPort.scale);
	}

}
