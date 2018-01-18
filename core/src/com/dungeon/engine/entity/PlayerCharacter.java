package com.dungeon.engine.entity;

import com.dungeon.engine.physics.Body;
import com.dungeon.game.GameState;
import com.dungeon.game.object.Tombstone;

public abstract class PlayerCharacter extends Character {

	protected PlayerCharacter(Body body) {
		super(body);
	}

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state, getPos());
		state.addEntity(tombstone);
	}
}
