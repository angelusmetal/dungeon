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
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.Resources;

import java.util.function.Predicate;

public abstract class PlayerEntity extends CreatureEntity {

	private int playerId;
	private Metronome stepMetronome;

	protected PlayerEntity(EntityPrototype prototype, Vector2 origin) {
		super(origin, prototype);
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
		if (getSelfImpulse().x == 0 && getSelfImpulse().y == 0) {
			if (getAnimation() != getAttackAnimation() || isAnimationFinished()) {
				updateAnimation(getIdleAnimation());
			}
		} else {
			updateAnimation(getWalkAnimation());
			stepMetronome.doAtInterval();
		}
	}

	@Override
	protected void onExpire() {
		getPlayer().getConsole().log("You have died", Color.GOLD);
	}

	public void fire() {
		if (!expired) {
			actionGate.attempt(0.25f, () -> {
				// FIXME Adding y=2 to prevent projectile from spawning inside bottom wall
				getPlayer().getWeapon().spawnEntities(getBody().getCenter(), getAim());
				updateAnimation(getAttackAnimation());
			});
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

}
