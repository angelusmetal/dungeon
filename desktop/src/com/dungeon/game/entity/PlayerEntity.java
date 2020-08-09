package com.dungeon.game.entity;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Util;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;

import java.util.function.Predicate;

import static com.dungeon.engine.controller.pov.PovToggle.vec2ToPov4;
import static com.dungeon.game.character.player.PlayerCharacterFactory.STEP_INTERVAL;

public abstract class PlayerEntity extends CreatureEntity {

	private int playerId;
	private Metronome stepMetronome;
	private float slowUntil = 0f;

	/** Current energy */
	private float energy = 100;
	/** Maximum energy recovery */
	private float maxEnergy = 100;
	/** How much energy is recovered per second */
	private float energyRecovery = 20;
	/** Indicate whether this character is continuously firing */
	private boolean firing;
	/** Direction where character is heading (for picking the right animation) */
	private PovDirection animationDirection;

	protected PlayerEntity(EntityPrototype prototype, Vector2 origin) {
		super(origin, prototype);
		stepMetronome = getStepMetronome();
	}

	protected abstract Metronome getStepMetronome();

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public static final Predicate<Entity> TARGET_PLAYER_CHARACTERS = entity -> entity instanceof PlayerEntity;
	public static final Predicate<Entity> HIT_PLAYERS = entity -> entity instanceof PlayerEntity || !(entity instanceof CreatureEntity);
	public static final Predicate<Entity> TARGET_NON_PLAYER_CHARACTERS = entity -> entity instanceof CreatureEntity && !(entity instanceof PlayerEntity);
	public static final Predicate<Entity> HIT_NON_PLAYERS = entity -> !(entity instanceof PlayerEntity);

	@Override
	public void think() {
		super.think();
		if (!isAttackAnimation() || isAnimationFinished()) {
			PovDirection newDirection = getAnimationDirection();
			if (newDirection != PovDirection.center) {
				updateXScale(animationDirection);
				animationDirection = newDirection;
			}
			if (getSelfImpulse().x == 0 && getSelfImpulse().y == 0) {
				updateAnimation(getIdleAnimation(animationDirection));
			} else {
				if (updateAnimation(getWalkAnimation(animationDirection))) {
					stepMetronome.reset(STEP_INTERVAL / 2f);
				}
				stepMetronome.doAtInterval();
			}
		}
		if (firing) {
			fire();
		}
		energy = Math.min(energy + energyRecovery * Engine.frameTime(), maxEnergy);
	}

	private PovDirection getAnimationDirection() {
		if (getAim().len2() > 0) {
			return vec2ToPov4(getAim(), 0.8f);
		} else {
			return vec2ToPov4(getMovement(), speed * 0.8f);
		}
	}

	/** Inverts the horizontal draw scale based on the movement vector */
	private void updateXScale(PovDirection direction) {
		if (direction == PovDirection.west) {
			// Only invert for west
			getDrawScale().x = Math.abs(getDrawScale().x) * -1;
		} else if (direction != PovDirection.center) {
			// Only update if direction is not center
			getDrawScale().x = Math.abs(getDrawScale().x);
		}
	}

	@Override
	protected void onExpire() {
		getPlayer().getConsole().log("You have died", Color.GOLD);
	}

	public void fire() {
		if (!expired) {
			Weapon weapon = getPlayer().getWeapon();
			// Attempt attack; will succeed only if cooldown is ready
			if (energy > weapon.energyDrain()) {
				actionGate.attempt(weapon.attackCooldown(), () -> {
					weapon.attack(getBody().getCenter(), getAim());
//					PovDirection animationDirection = getAnimationDirection();
					updateXScale(animationDirection);
					updateAnimation(getAttackAnimation(animationDirection));
					slowUntil = Engine.time() + weapon.attackCooldown();
					energy -= weapon.energyDrain();
				});
			}
		}
	}

	private final float interactDistance = Util.length2(20f);
	private final Vector2 interactArea = new Vector2(30f, 30f);

	public void interact() {
		if (!expired) {
			Body area = Body.centered(getFace().cpy().setLength2(interactDistance).add(getOrigin()), interactArea);
			Engine.entities.area(area).forEach(e -> e.signal(this));
		}
	}

	abstract protected Animation<Sprite> getIdleAnimation(PovDirection direction);
	abstract protected Animation<Sprite> getWalkAnimation(PovDirection direction);
	abstract protected Animation<Sprite> getAttackAnimation(PovDirection direction);
	abstract protected boolean isAttackAnimation();

	public Player getPlayer() {
		return Players.get(playerId);
	}

	public void heal(int amount) {
		health += amount;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

	public Vector2 getEffectiveSelfImpulse() {
		return Engine.time() < slowUntil ? Vector2.Zero : getSelfImpulse();
	}

	public float getEnergy() {
		return energy;
	}

	public float getMaxEnergy() {
		return maxEnergy;
	}

	public void setFiring(boolean firing) {
		this.firing = firing;
	}
}
