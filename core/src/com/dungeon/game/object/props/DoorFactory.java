package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class DoorFactory {

	private static final String HORIZONTAL_DOOR = "door_horizontal";
	private static final String HORIZONTAL_DOOR_OPENING = "door_horizontal_opening";
	private static final String VERTICAL_DOOR = "door_vertical";
	private static final String VERTICAL_DOOR_OPENING = "door_vertical_opening";

	private final EntityPrototype verticalClosed;
	private final EntityPrototype verticalOpening;
	private final EntityPrototype horizontalClosed;
	private final EntityPrototype horizontalOpening;

	public DoorFactory() {
		horizontalClosed = ResourceManager.getPrototype(HORIZONTAL_DOOR);
		horizontalOpening = ResourceManager.getPrototype(HORIZONTAL_DOOR_OPENING);
		verticalClosed = ResourceManager.getPrototype(VERTICAL_DOOR);
		verticalOpening = ResourceManager.getPrototype(VERTICAL_DOOR_OPENING);
	}

	public Entity buildHorizontal(Vector2 origin) {
		return buildDoor(origin, horizontalClosed, horizontalOpening);
	}

	public Entity buildVertical(Vector2 origin) {
		return buildDoor(origin, verticalClosed, verticalOpening);
	}

	private Entity buildDoor(Vector2 origin, EntityPrototype closed, EntityPrototype opening) {
		return new Entity(closed, origin) {
			@Override public void onHit() {
				Rand.doBetween(2, 5, () ->
						GameState.addEntity(GameState.build(EntityType.WOOD_PARTICLE, getOrigin()))
				);
			}
			@Override public void onExpire() {
				Entity openingDoor = new Entity(opening, getOrigin());
				GameState.addEntity(openingDoor);
			}
// TODO Re-enable this once we can interact some other way with the props
//			@Override public boolean canBeHurt() {
//				return false;
//			}
		};
	}
}
