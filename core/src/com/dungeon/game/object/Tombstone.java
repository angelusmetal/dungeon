package com.dungeon.game.object;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class Tombstone extends Entity<Tombstone.AnimationType> {

	public Tombstone(GameState state, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
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
