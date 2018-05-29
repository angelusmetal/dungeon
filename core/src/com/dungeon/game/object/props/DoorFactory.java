package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class DoorFactory {

	private static final Vector2 HORIZONTAL_BOUNDING_BOX = new Vector2(7, 96);
	private static final Vector2 VERTICAL_BOUNDING_BOX = new Vector2(48, 48);
	private static final Vector2 DRAW_OFFSET = new Vector2(24, 24);

	private final EntityPrototype verticalClosed;
	private final EntityPrototype verticalOpening;
	private final EntityPrototype horizontalClosed;
	private final EntityPrototype horizontalOpening;

	public DoorFactory() {
		horizontalClosed = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().doorHorizontal().animation)
				.boundingBox(HORIZONTAL_BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET);
		horizontalOpening = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().doorHorizontal().animation)
				.boundingBox(HORIZONTAL_BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(1f)
				.zSpeed(40f)
				.with(Traits.fadeOut(1f));
		verticalClosed = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().doorVertical().animation)
				.boundingBox(VERTICAL_BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET);
		verticalOpening = new EntityPrototype()
				.animation(() -> GameState.getLevelTileset().doorVertical().animation)
				.boundingBox(VERTICAL_BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(1f)
				.zSpeed(40f)
				.with(Traits.fadeOut(1f));
	}

	public Entity buildHorizontal(Vector2 origin) {
		return buildDoor(origin, horizontalClosed, horizontalOpening);
	}

	public Entity buildVertical(Vector2 origin) {
		return buildDoor(origin, verticalClosed, verticalOpening);
	}

	private Entity buildDoor(Vector2 origin, EntityPrototype closed, EntityPrototype opening) {
		return new Entity(origin, closed) {
			@Override public boolean isSolid() {
				return true;
			}
			@Override public void onHit() {
				Rand.doBetween(2, 5, () ->
						GameState.addEntity(GameState.build(EntityType.WOOD_PARTICLE, getPos()))
				);
			}
			@Override public void onExpire() {
				Entity openingDoor = new Entity(getPos(), opening) {
					@Override public boolean canBeHurt() {
						return false;
					}
				};
				GameState.addEntity(openingDoor);
			}
// TODO Re-enable this once we can interact some other way with the props
//			@Override public boolean canBeHurt() {
//				return false;
//			}
		};
	}
}
