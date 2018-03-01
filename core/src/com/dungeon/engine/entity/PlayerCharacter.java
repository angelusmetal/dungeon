package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Quaternion;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.GameState;
import com.dungeon.game.object.Tombstone;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PlayerCharacter extends Character {

	public static Quaternion TORCH_LIGHT = new Quaternion(1, 0.8f, 0.4f, 0.8f);

	protected PlayerCharacter(Body body) {
		super(body);
		lightRadius = 32;
		lightColor = TORCH_LIGHT;
	}

	public static final Function<Entity, Boolean> IS_PLAYER = (entity) -> entity instanceof PlayerCharacter;
	public static final Function<Entity, Boolean> IS_NON_PLAYER = (entity) -> entity instanceof Character && !(entity instanceof PlayerCharacter);

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state, getPos());
		state.addEntity(tombstone);
	}

	public void heal(int amount) {
		health += amount;
		if (health > maxHealth) {
			health = maxHealth;
		}
	}
}
