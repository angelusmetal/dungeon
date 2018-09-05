package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.resource.Resources;

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
		horizontalClosed = Resources.prototypes.get(HORIZONTAL_DOOR);
		horizontalOpening = Resources.prototypes.get(HORIZONTAL_DOOR_OPENING);
		verticalClosed = Resources.prototypes.get(VERTICAL_DOOR);
		verticalOpening = Resources.prototypes.get(VERTICAL_DOOR_OPENING);
	}

	public Entity buildHorizontal(Vector2 origin) {
		return buildDoor(origin, horizontalClosed, horizontalOpening);
	}

	public Entity buildVertical(Vector2 origin) {
		return buildDoor(origin, verticalClosed, verticalOpening);
	}

	private Entity buildDoor(Vector2 origin, EntityPrototype closed, EntityPrototype opening) {
		return new DungeonEntity(closed, origin) {
			@Override public void onSignal(Entity emitter) {
				if (!expired) {
					Entity openingDoor = new Entity(opening, getOrigin());
					Engine.entities.add(openingDoor);
					expire();
				}
			}
		};
	}
}