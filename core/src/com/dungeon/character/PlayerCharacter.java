package com.dungeon.character;

import com.dungeon.GameState;
import com.dungeon.objects.Tombstone;

public class PlayerCharacter extends Character {

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state);
		tombstone.moveTo(getPos());
		state.addEntity(tombstone);
	}
}
