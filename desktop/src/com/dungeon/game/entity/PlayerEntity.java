package com.dungeon.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.util.Util;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.Lights;

import java.util.function.Predicate;

public abstract class PlayerEntity extends CreatureEntity {

	static private Light TORCH_LIGHT = new Light(160, new Color(0.25f, 0.2f, 0.1f, 0.2f), Lights.NORMAL, Light.torchlight());

	private int playerId;

	protected PlayerEntity(EntityPrototype prototype, Vector2 origin) {
		super(origin, prototype);
		light = TORCH_LIGHT;
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
			if (getCurrentAnimation().getAnimation() != getAttackAnimation() || getCurrentAnimation().isFinished()) {
				updateCurrentAnimation(getIdleAnimation());
			}
		} else {
			updateCurrentAnimation(getWalkAnimation());
		}
	}

	@Override
	protected void onExpire() {
		getPlayer().getConsole().log("You have died", Color.GOLD);
	}

	public void fire() {
		if (!expired) {
			fireCooldown.attempt(Engine.time(), () -> {
				getPlayer().getWeapon().spawnEntities(getOrigin(), getAim());
				updateCurrentAnimation(getAttackAnimation());
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
