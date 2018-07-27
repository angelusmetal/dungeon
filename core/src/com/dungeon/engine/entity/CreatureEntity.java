package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

public abstract class CreatureEntity extends Entity implements Movable, Drawable {

	private Vector2 aim = new Vector2(1, 0);

	protected CooldownTrigger fireCooldown = new CooldownTrigger(0.25f); // Default is fire every 0.25 seconds

	public CreatureEntity(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		solid = true;
		canBeHit = true;
		canBeHurt = true;
	}

	@Override
	public void think() {
		if (getAim().x != 0) {
			setInvertX(getAim().x < 0);
		}
		if (getSelfImpulse().len() > 0.5) {
			aim(getSelfImpulse());
		}
	}

	public void aim(Vector2 vector) {
		aim.set(vector);
	}

	public Vector2 getAim() {
		return aim;
	}

	public void drawHealthbar(SpriteBatch batch, ViewPort viewPort) {
		// Draw health bar
//		viewPort.draw(batch, GameState.getTilesetManager().getHudTileset().HEALTH_BAR, getPos().x - getDrawOffset().x, getPos().y - getDrawOffset().y + getBoundingBox().y + z, getBoundingBox().x * health/maxHealth, 2);
		viewPort.draw(batch, GameState.getTilesetManager().getHudTileset().HEALTH_BAR, getPos().x - getBoundingBox().x / 2, getPos().y + getBoundingBox().y / 2 + 4 + z, getBoundingBox().x * health/maxHealth, 2);
	}
}
