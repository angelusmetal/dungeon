package com.dungeon.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.dungeon.game.resource.Resources;

import java.util.function.Predicate;

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

	protected PlayerEntity(EntityPrototype prototype, Vector2 origin) {
		super(origin, prototype);
		// Spawn cute dust clouds when walking
		EntityPrototype dust_cloud = Resources.prototypes.get("dust_cloud_2");
		stepMetronome = new Metronome(0.4f, () -> Engine.entities.add(new Entity(dust_cloud, this.getOrigin().cpy().add(0, 1))));
	}

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
		if (getAnimation() != getAttackAnimation() || isAnimationFinished()) {
			updateXScale();
			if (getSelfImpulse().x == 0 && getSelfImpulse().y == 0) {
				updateAnimation(getIdleAnimation());
			} else {
				updateAnimation(getWalkAnimation());
				stepMetronome.doAtInterval();
			}
		}
		if (firing) {
			fire();
		}
		energy = Math.min(energy + energyRecovery * Engine.frameTime(), maxEnergy);
	}

	/** Inverts the horizontal draw scale based on the movement vector */
	private void updateXScale() {
		if (getAim().x != 0) {
			getDrawScale().x = Math.abs(getDrawScale().x) * getAim().x < 0 ? -1 : 1;
		} else if (getMovement().x != 0) {
			getDrawScale().x = Math.abs(getDrawScale().x) * getMovement().x < 0 ? -1 : 1;
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
					weapon.spawnEntities(getBody().getCenter(), getAim());
					updateAnimation(getAttackAnimation());
					slowUntil = Engine.time() + weapon.attackCooldown();
					energy -= weapon.energyDrain();
				});
			}
		}
	}

	private final float interactDistance = Util.length2(20f);
	private final Vector2 interactArea = new Vector2(40f, 40f);

	public void interact() {
		if (!expired) {
			Body area = Body.centered(getAim().cpy().setLength2(interactDistance).add(getOrigin()), interactArea);
			Engine.entities.area(area).forEach(e -> e.signal(this));
		}
	}

	abstract protected Animation<TextureRegion> getAttackAnimation();
	abstract protected Animation<TextureRegion> getIdleAnimation();
	abstract protected Animation<TextureRegion> getWalkAnimation();

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
