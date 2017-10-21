package com.dungeon.game.object;

import com.dungeon.game.GameState;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;

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
