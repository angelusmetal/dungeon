package com.dungeon.game.object.door;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class HorizontalDoorFactory implements EntityFactory.EntityTypeFactory {

	private static final Vector2 BOUNDING_BOX = new Vector2(7, 96);
	private static final Vector2 DRAW_OFFSET = new Vector2(24, 24);

	private final EntityPrototype closed;
	private final EntityPrototype opening;

	public HorizontalDoorFactory() {
		closed = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().doorHorizontal().animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET);
		opening = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().doorHorizontal().animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(1f)
				.zSpeed(40f)
				.with(Traits.fadeOut(1f));
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(origin, closed) {
			// TODO Re-enable this once we can interact some other way with the door
			@Override public boolean isSolid() {
				return true;
			}
//			@Override public boolean canBeHurt() {
//				return false;
//			}
			@Override public void onExpire() {
				Entity openingDoor = new Entity(getPos(), opening) {
					@Override public boolean canBeHurt() {
						return false;
					}
				};
				GameState.addEntity(openingDoor);
			}
		};
	}
}
