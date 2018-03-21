package com.dungeon.game.object;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.TombstoneTileset;

public class Tombstone extends Entity<Tombstone.AnimationType> {

	public enum AnimationType {
		SPAWN;
	}

	public static class Factory implements EntityFactory.EntityTypeFactory {

		private GameState state;

		public Factory(GameState state) {
			this.state = state;
		}

		@Override
		public Entity<?> build(Vector2 origin) {
			Tombstone entity = new Tombstone(origin);
			entity.setCurrentAnimation(new GameAnimation<>(AnimationType.SPAWN, ResourceManager.instance().getAnimation(TombstoneTileset.SPAWN, TombstoneTileset::spawn), state.getStateTime()));
			return entity;
		}
	}

	public Tombstone(Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
	}

	@Deprecated // TODO have the death use the factory or remove the factory, but let's not have both
	public Tombstone(GameState state, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
		setCurrentAnimation(new GameAnimation<>(AnimationType.SPAWN, ResourceManager.instance().getAnimation(TombstoneTileset.SPAWN, TombstoneTileset::spawn), state.getStateTime()));
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
