package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public abstract class Character extends Entity implements Movable, Drawable {

	private Vector2 aim = new Vector2(1, 0);

	protected CooldownTrigger fireCooldown = new CooldownTrigger(0.25f); // Default is fire every 0.25 seconds

	public Character(Body body) {
		super(body);
	}

	@Override
	public void think(GameState state) {
		if (getSelfMovement().x != 0) {
			setInvertX(getSelfMovement().x < 0);
		}
		if (getSelfMovement().x == 0 && getSelfMovement().y == 0) {
			if (getIdleAnimation() != getCurrentAnimation().getAnimation()) {
				setCurrentAnimation(new GameAnimation(getIdleAnimation(), state.getStateTime()));
			}
		} else {
			if (getWalkAnimation() != getCurrentAnimation().getAnimation()) {
				setCurrentAnimation(new GameAnimation(getWalkAnimation(), state.getStateTime()));
			}
		}
		if (getSelfMovement().len() > 0.5) {
			aim.set(getSelfMovement());
		}
	}

	abstract protected Animation<TextureRegion> getAttackAnimation();
	abstract protected Animation<TextureRegion> getIdleAnimation();
	abstract protected Animation<TextureRegion> getWalkAnimation();

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
				Projectile projectile = createProjectile(state, getPos().cpy().mulAdd(aim, 2));
				if (projectile != null) {
					projectile.setSelfMovement(aim);
					// Extra offset to make projectiles appear in the character's hands
					//projectile.getPos().y -= 8;
					state.addEntity(projectile);
					setCurrentAnimation(new GameAnimation(getAttackAnimation(), state.getStateTime()));
				}
			});
		}
	}

	protected Projectile createProjectile(GameState state, Vector2 pos) {
		return null;
	}

	public void drawHealthbar(GameState state, SpriteBatch batch, ViewPort viewPort) {
		// Draw health bar
		viewPort.draw(batch, state.getTilesetManager().getHudTileset().HEALTH_BAR, getPos().x- getBoundingBox().x / 2, getPos().y + getBoundingBox().y / 2 + z, getBoundingBox().x * health/maxHealth, 2);
	}
}
