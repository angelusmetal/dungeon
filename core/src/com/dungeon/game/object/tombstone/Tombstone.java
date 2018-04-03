package com.dungeon.game.object.tombstone;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class Tombstone extends Entity {

	public static class Factory implements EntityFactory.EntityTypeFactory {

		final GameState state;
		final Animation<TextureRegion> animation;

		public Factory(GameState state) {
			this.state = state;
			animation = ResourceManager.instance().getAnimation(TombstoneSheet.SPAWN, TombstoneSheet::spawn);
		}

		@Override
		public Tombstone build(Vector2 origin) {
			return new Tombstone(this, origin);
		}
	}

	Tombstone(Factory factory, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
		setCurrentAnimation(new GameAnimation(factory.animation, factory.state.getStateTime()));
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
