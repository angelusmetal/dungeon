package com.dungeon.engine.entity;

import com.dungeon.engine.physics.Body;
import com.dungeon.game.GameState;
import com.dungeon.game.object.Tombstone;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PlayerCharacter extends Character {

	protected PlayerCharacter(Body body) {
		super(body);
	}

	public static final Function<Entity, Boolean> IS_PLAYER = (entity) -> entity instanceof PlayerCharacter;
	public static final Function<Entity, Boolean> IS_NON_PLAYER = (entity) -> entity instanceof Character && !(entity instanceof PlayerCharacter);

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state, getPos());
		state.addEntity(tombstone);
	}
}
