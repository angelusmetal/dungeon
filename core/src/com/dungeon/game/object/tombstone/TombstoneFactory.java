package com.dungeon.game.object.tombstone;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class TombstoneFactory implements EntityFactory.EntityTypeFactory {

	private static final String SPAWN = "tombstone_spawn";

	final Animation<TextureRegion> animation;
	private final EntityPrototype object;

	public TombstoneFactory() {
		Vector2 boundingBox = new Vector2(10, 10);
		Vector2 drawOffset = new Vector2(16, 16);

		animation = ResourceManager.getAnimation(SPAWN);
		object = new EntityPrototype()
				.animation(animation)
				.boundingBox(boundingBox)
				.drawOffset(drawOffset);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(origin, object) {
			@Override
			public void onHit() {
				Rand.doBetween(2, 5, () ->
						GameState.addEntity(GameState.build(EntityType.STONE_PARTICLE, getPos())));
			}
			@Override
			public boolean isSolid() {
				return true;
			}
		};
	}
}
