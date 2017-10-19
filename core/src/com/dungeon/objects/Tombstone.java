package com.dungeon.objects;

import com.dungeon.GameState;
import com.dungeon.animation.GameAnimation;
import com.dungeon.character.Entity;

public class Tombstone extends Entity<Tombstone.AnimationType> {

	public Tombstone(GameState state) {
		setCurrentAnimation(new GameAnimation<>(AnimationType.SPAWN, state.getTilesetManager().getTombstoneTileset().TOMBSTONE_SPAWN_ANIMATION, state.getStateTime()));
	}

	public enum AnimationType {
		SPAWN;
	}

	@Override
	public boolean isExpired(float time) {
		return expired;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
