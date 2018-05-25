package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Light;
import com.dungeon.game.state.GameState;

import java.util.function.Predicate;

public abstract class PlayerCharacter extends Character {

	static private Light TORCH_LIGHT = new Light(160, new Color(0.25f, 0.2f, 0.1f, 0.2f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

	protected PlayerCharacter(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
		light = TORCH_LIGHT;
	}

	public static final Predicate<Entity> IS_PLAYER = entity -> entity instanceof PlayerCharacter;
	public static final Predicate<Entity> IS_NON_PLAYER = entity -> entity instanceof Character && !(entity instanceof PlayerCharacter);

	@Override
	public void think() {
		super.think();
		if (getSelfImpulse().x == 0 && getSelfImpulse().y == 0) {
			if (getCurrentAnimation().getAnimation() != getAttackAnimation() || getCurrentAnimation().isFinished()) {
				updateCurrentAnimation(getIdleAnimation());
			}
		} else {
			updateCurrentAnimation(getWalkAnimation());
		}
	}

	@Override
	protected void onExpire() {
		GameState.console().log("You have died", Color.GOLD);
	}

	public void fire() {
		if (!expired) {
			fireCooldown.attempt(GameState.time(), () -> {
				Entity projectile = createProjectile(getPos().cpy().mulAdd(getAim(), 2));
				if (projectile != null) {
					projectile.impulse(getAim().cpy().setLength(projectile.speed));
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

	abstract protected Animation<TextureRegion> getAttackAnimation();
	abstract protected Animation<TextureRegion> getIdleAnimation();
	abstract protected Animation<TextureRegion> getWalkAnimation();

	public void heal(int amount) {
		health += amount;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

}
