package com.dungeon.engine.entity;

import com.dungeon.game.GameState;
import com.dungeon.game.object.Tombstone;

public abstract class PlayerCharacter extends Character {

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state);
		tombstone.moveTo(getPos());
		state.addEntity(tombstone);
	}
}
