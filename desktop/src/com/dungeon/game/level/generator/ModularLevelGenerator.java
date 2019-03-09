package com.dungeon.game.level.generator;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.level.Level;
import com.dungeon.game.level.Room;
import com.dungeon.game.level.RoomPrototype;
import com.dungeon.game.level.TileType;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * A level generator that generates levels by connecting a set of pre-built modules (rooms).
 */
public class ModularLevelGenerator implements LevelGenerator {

	private int width;
	private int height;
	private TileType[][] tiles;
	private int maxRoomSeparation = 8;
	private int minRoomSeparation = 2;
	private List<Room> rooms = new ArrayList<>();
	private Environment environment;
	private Map<String, Integer> roomOccurrences = new HashMap<>();

	public enum Direction {
		UP (0, 1),
		RIGHT (1, 0),
		DOWN (0, -1),
		LEFT (-1, 0);
		Direction(int x, int y) {
			this.x = x;
			this.y = y;
		}
		private final int x;
		private final int y;
		public Direction opposite() {
			switch (this) {
				case UP:
					return DOWN;
				case DOWN:
					return UP;
				case LEFT:
					return RIGHT;
				default:
					return LEFT;
			}
		}
	}

	public static class ConnectionPoint {
		public GridPoint2 origin;
		public Direction direction;
		public boolean visited = false;
		public boolean active = false;
		public ConnectionPoint (GridPoint2 origin, Direction direction) {
			this.origin = origin;
			this.direction = direction;
		}

		@Override
		public String toString() {
			return "origin: [" + origin + "], visited: " + visited + ", active: " + active;
		}
	}

	public ModularLevelGenerator(Environment environment, int width, int height) {
		this.environment = environment;
		this.width = width;
		this.height = height;
		this.tiles = new TileType[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = TileType.VOID;
			}
		}
	}

	public Level generateLevel() {
		// Generate rooms
		while (
				// repeat if no rooms could be placed
				rooms.isEmpty() ||
				// or no exit could be placed
				roomOccurrences.getOrDefault("exit_room", 0) == 0) {

			roomOccurrences.clear();
			// Pick a random position to start (excluding border rows/columns)
			GridPoint2 start = new GridPoint2(
					Rand.between(5, width - 5),
					Rand.between(5, height - 5));

			// Depending on the closest edge (bottom, left, right, up) pick the opposite edge as search direction to grow the room
			Direction direction;
			int closestEdgeX = Math.min(start.x, width - start.x);
			int closestEdgeY = Math.min(start.y, height - start.y);
			if (closestEdgeX < closestEdgeY) {
				direction = closestEdgeX == start.x ? Direction.RIGHT : Direction.LEFT;
			} else {
				direction = closestEdgeY == start.y ? Direction.UP : Direction.DOWN;
			}
			// Generate rooms
			generateRooms(start, direction);
		}

		Level level = new Level(width, height);
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				level.setAnimation(x, y, getTile(x, y, environment.getTileset()));
				level.setSolid(x, y, !tiles[x][y].isFloor());
			}
		}
		level.setRooms(rooms);
		generateEntities(level.getEntityPlaceholders());

		return level;
	}

	private void generateEntities(List<EntityPlaceholder> placeholders) {
		List<String> monsterTypes = environment.getMonsters();

		// Create player spawn points in the starting room
		rooms.get(0).spawnPoints.forEach(pos -> placeholders.add(new EntityPlaceholder(EntityType.PLAYER_SPAWN, pos)));

		// Create monsters in each room, to begin with
		for (int r = 1; r < rooms.size(); ++r) {
			Room room = rooms.get(r);
			// Create a random amount of monsters in each room (less likely every level)
			final int skip = Math.max(0, Rand.nextInt(room.spawnPoints.size()) - Game.getLevelCount());
			room.spawnPoints.stream().skip(skip).forEach(pos -> placeholders.add(new EntityPlaceholder(Rand.pick(monsterTypes), pos)));

		}
		// Add placeholders
		for (Room room : rooms) {
			placeholders.addAll(room.placeholders);
		}

	}

	private Animation<TextureRegion> getTile(int x, int y, Tileset tileset) {
		// TODO Make this work with level itself

		if (tiles[x][y] == TileType.FLOOR) {
			return tileset.floor();
		} else if (tiles[x][y] == TileType.WALL_DECORATION_1) {
			return tileset.wallDecoration1();
		} else if (tiles[x][y] == TileType.WALL_DECORATION_2) {
			return tileset.wallDecoration2();
		} else if (tiles[x][y] == TileType.WALL_DECORATION_3) {
			return tileset.wallDecoration3();
		} else if (tiles[x][y] == TileType.WALL_DECORATION_4) {
			return tileset.wallDecoration4();
		}

		boolean freeUp = y > 0 && tiles[x][y-1].isFloor();
		boolean freeDown = y < height - 1 && tiles[x][y+1].isFloor();
		boolean freeLeft = x > 0 && tiles[x-1][y].isFloor();
		boolean freeRight = x < width - 1 && tiles[x+1][y].isFloor();
		boolean freeUpLeft = y > 0 && x > 0 && tiles[x-1][y-1].isFloor();
		boolean freeUpRight = y > 0 && x < width - 1 && tiles[x+1][y-1].isFloor();
		boolean freeDownLeft = y < height - 1 && x > 0 && tiles[x-1][y+1].isFloor();
		boolean freeDownRight = y < height - 1 && x < width - 1 && tiles[x+1][y+1].isFloor();

		if (freeUp) {
			if (freeLeft) {
				return tileset.convexUpperRight();
			} else if (freeRight) {
				return tileset.convexUpperLeft();
			} else {
				return tileset.concaveUpper();
			}
		} else if (freeLeft) {
			if (freeDown) {
				return tileset.convexLowerRight();
			} else {
				return tileset.concaveRight();
			}
		} else if (freeDown) {
			if (freeRight) {
				return tileset.convexLowerLeft();
			} else {
				return tileset.concaveLower();
			}
		} else if (freeRight) {
			return tileset.concaveLeft();
		} else if (freeUpLeft) {
			return tileset.concaveUpperRight();
		} else if (freeUpRight) {
			return tileset.concaveUpperLeft();
		} else if (freeDownLeft) {
			return tileset.concaveLowerRight();
		} else if (freeDownRight) {
			return tileset.concaveLowerLeft();
		}
		return tileset.out();
	}

	private static class Frame {
		final GridPoint2 origin;
		final Direction direction;
		final ConnectionPoint originPoint;
		final int roomSeparation;
		final int generation;
		Frame(GridPoint2 origin, Direction direction, ConnectionPoint originPoint, int roomSeparation, int generation) {
			this.origin = origin;
			this.direction = direction;
			this.originPoint = originPoint;
			this.roomSeparation = roomSeparation;
			this.generation = generation;
		}
	}

	private void generateRooms(GridPoint2 origin, Direction direction) {
		Stack<Frame> stack = new Stack<>();
		stack.push(new Frame(origin, direction, null, 0, 1));
		while (!stack.isEmpty()) {
			Frame frame = stack.pop();
			// Check the available size, depending on the direction
			Room room = attemptRoom(frame.origin, frame.generation, frame.direction);
			if (room != null) {
				if (frame.originPoint != null) {
					// Add props
					addDoor(frame, room);
				}
				// Pick each connection point and attempt to place a room
				room.connectionPoints.stream().filter(point -> !point.visited).forEach(point -> {
					// Attempt to generate a room in that direction (at a random separation)
					int roomSeparation = Rand.between(minRoomSeparation, maxRoomSeparation);
					GridPoint2 newOrigin = point.origin.cpy().add(point.direction.x * roomSeparation, point.direction.y * roomSeparation);
					stack.push(new Frame(newOrigin, point.direction, point, roomSeparation, frame.generation + 1));
				});
				if (frame.originPoint != null) {
					frame.originPoint.active = true;
					// Make the connecting tile walkable
					for (int i = 0; i <= frame.roomSeparation; ++i) {
						int xi = frame.originPoint.origin.x + frame.originPoint.direction.x * i;
						int yi = frame.originPoint.origin.y + frame.originPoint.direction.y * i;
						tiles[xi][yi] = TileType.FLOOR;
						if (i % 2 == 0) {
							room.placeholders.add(new EntityPlaceholder("gold_light_small", new Vector2(xi + 0.5f, yi + 0.5f)));
						}
					}
					frame.originPoint.visited = true;
				}
			}
		}
	}

	private void addDoor(Frame frame, Room room) {
		String doorType = frame.direction == Direction.UP || frame.direction == Direction.DOWN ? EntityType.DOOR_VERTICAL : EntityType.DOOR_HORIZONTAL;
		EntityPlaceholder door = new EntityPlaceholder(doorType, new Vector2(frame.originPoint.origin.x + 0.5f, frame.originPoint.origin.y));
		room.placeholders.add(door);
		if (frame.roomSeparation > 0) {
			door = new EntityPlaceholder(doorType, new Vector2(frame.originPoint.origin.x + 0.5f + frame.roomSeparation * frame.direction.x, frame.originPoint.origin.y + frame.roomSeparation * frame.direction.y));
			room.placeholders.add(door);
		}
	}

	private Room attemptRoom(GridPoint2 origin, int generation, Direction direction) {
		if (origin.x < 2 || origin.x >= width - 2 || origin.y < 2 || origin.y >= height - 2) {
			return null;
		}

		// Attempt to place each room (in random order) until one succeeds
		Collections.shuffle(environment.getRooms());
		for (RoomPrototype prototype : environment.getRooms()) {
			Optional<ConnectionPoint> entryPoint = prototype.getConnections().stream().filter(d -> d.direction == direction.opposite()).findFirst();
			if (entryPoint.isPresent()) {
				ConnectionPoint p = entryPoint.get();
				Room room = new Room(origin.x - p.origin.x, origin.y - p.origin.y, generation + 1, prototype);
				if (canPlaceRoom(room)) {
					placeRoom(room);
					return room;
				}
			}
		}
		// Could not place any of the known rooms
		return null;
	}

	private boolean canPlaceRoom(Room room) {
		if (room.left < 2 || room.bottom < 2 || room.left + room.width >= width - 2 || room.bottom + room.height >= height - 2) {
			return false;
		}

		for (int x = room.left - 2; x <= room.left + room.width + 2; ++x) {
			for (int y = room.bottom - 2; y <= room.bottom + room.height + 2; ++y) {
				if (tiles[x][y] != TileType.VOID) {
					return false;
				}
			}
		}
		// If the max amount of this type of room has been placed, then it cannot be placed
		if (roomOccurrences.getOrDefault(room.prototype.getName(), 0) >= room.prototype.getMaxOccurrences()) {
			return false;
		}
		// If the minimum depth for this room has not yet been reached, it cannot be placed
		if (room.generation < room.prototype.getMinDepth()) {
			return false;
		}
		return true;
	}

	private void placeRoom(Room room) {
		rooms.add(room);
		roomOccurrences.compute(room.prototype.getName(), (name, occurrences) -> occurrences == null ? 1 : occurrences + 1);
		for (int x = 0; x < room.width; ++x) {
			if (room.height >= 0)
				System.arraycopy(room.tiles[x], 0, tiles[x + room.left], room.bottom, room.height);
		}
	}

}
