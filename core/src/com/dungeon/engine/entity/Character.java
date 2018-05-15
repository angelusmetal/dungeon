package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public abstract class Character extends Entity implements Movable, Drawable {

	private Vector2 aim = new Vector2(1, 0);

	protected CooldownTrigger fireCooldown = new CooldownTrigger(0.25f); // Default is fire every 0.25 seconds

	public Character(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
	}

	@Override
	public void think() {
		if (aim.x != 0) {
			setInvertX(aim.x < 0);
		}
		if (getSelfImpulse().x == 0 && getSelfImpulse().y == 0) {
			updateCurrentAnimation(getIdleAnimation());
		} else {
			updateCurrentAnimation(getWalkAnimation());
		}
		if (getSelfImpulse().len() > 0.5) {
			aim.set(getSelfImpulse());
		}
	}

	abstract protected Animation<TextureRegion> getAttackAnimation();
	abstract protected Animation<TextureRegion> getIdleAnimation();
	abstract protected Animation<TextureRegion> getWalkAnimation();

	@Override
	public boolean isSolid() {
		return true;
	}

	public void aim(Vector2 vector) {
		aim.set(vector);
	}

	public Vector2 getAim() {
		return aim;
	}

	public void fire() {
		if (!expired) {
			fireCooldown.attempt(GameState.time(), () -> {
				Entity projectile = createProjectile(getPos().cpy().mulAdd(aim, 2));
				if (projectile != null) {
					projectile.impulse(aim.cpy().setLength(projectile.speed));
					// Extra offset to make projectiles appear in the character's hands
					//projectile.getPos().y -= 8;
					GameState.addEntity(projectile);
					updateCurrentAnimation(getAttackAnimation());
				}
			});
		}
	}

	protected Entity createProjectile(Vector2 pos) {
		return null;
	}

	public void drawHealthbar(SpriteBatch batch, ViewPort viewPort) {
		// Draw health bar
//		viewPort.draw(batch, GameState.getTilesetManager().getHudTileset().HEALTH_BAR, getPos().x - getDrawOffset().x, getPos().y - getDrawOffset().y + getBoundingBox().y + z, getBoundingBox().x * health/maxHealth, 2);
		viewPort.draw(batch, GameState.getTilesetManager().getHudTileset().HEALTH_BAR, getPos().x - getBoundingBox().x / 2, getPos().y + getBoundingBox().y / 2 + 4 + z, getBoundingBox().x * health/maxHealth, 2);
	}
}
