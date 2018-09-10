package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.tileset.Environment;
import com.dungeon.game.tileset.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class ProceduralLevelGenerator {

	private int width;
	private int height;
	private TileType[][] tiles;
	private int maxRoomSeparation = 8;
	private int minRoomSeparation = 2;
	private List<Room> rooms = new ArrayList<>();
	private Environment environment;

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
		Vector2 coords;
		Direction direction;
		boolean visited = false;
		boolean active = false;
		public ConnectionPoint (int x, int y, Direction direction) {
			this.coords = new Vector2(x, y);
			this.direction = direction;
		}

		@Override
		public String toString() {
			return "coords: [" + coords + "], visited: " + visited + ", active: " + active;
		}
	}

	public ProceduralLevelGenerator(Environment environment, int width, int height) {
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
		while (rooms.isEmpty()) {
			// Pick a random position to start (excluding border rows/columns)
			int startX = Rand.between(5, width - 5);
			int startY = Rand.between(5, height - 5);

			// Depending on the closest edge (bottom, left, right, up) pick the opposite edge as search direction to grow the room
			Direction direction;
			int closestEdgeX = Math.min(startX, width - startX);
			int closestEdgeY = Math.min(startY, height - startY);
			if (closestEdgeX < closestEdgeY) {
				direction = closestEdgeX == startX ? Direction.RIGHT : Direction.LEFT;
			} else {
				direction = closestEdgeY == startY ? Direction.UP : Direction.DOWN;
			}
			generateRooms(startX, startY, direction);
		}

		Tile[][] map = new Tile[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				map[x][y] = getTile(x, y, environment.getTileset());
			}
		}

		Level level = new Level();
		level.map = map;
		level.rooms = rooms;
		level.walkableTiles = tiles;
		level.entityPlaceholders = generateEntities();

		return level;
	}

	private List<EntityPlaceholder> generateEntities() {
		List<String> monsterTypes = environment.getMonsters();
		List<EntityPlaceholder> placeholders = new ArrayList<>();

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

		// Pick one of the furthest-placed rooms and place the exit there
		int farthest = rooms.stream().map(r -> r.generation).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("Could not find farthest room"));
		Room exitRoom = rooms.stream().filter(r -> r.generation == farthest).findFirst().orElseThrow(() -> new RuntimeException("Could not find farthest room"));
		Vector2 exitPosition = Rand.pick(exitRoom.spawnPoints);
		placeholders.add(new EntityPlaceholder(EntityType.EXIT, exitPosition));

		return placeholders;
	}

	private Tile getTile(int x, int y, Tileset tileset) {

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
		final int x;
		final int y;
		final Direction direction;
		final ConnectionPoint originPoint;
		final int roomSeparation;
		final int generation;
		Frame(int x, int y, Direction direction, ConnectionPoint originPoint, int roomSeparation, int generation) {
			this.x = x;
			this.y = y;
			this.direction = direction;
			this.originPoint = originPoint;
			this.roomSeparation = roomSeparation;
			this.generation = generation;
		}
	}

	private void generateRooms(int x, int y, Direction direction) {
		Stack<Frame> stack = new Stack<>();
		stack.push(new Frame(x, y, direction, null, 0, 1));
		while (!stack.isEmpty()) {
			Frame frame = stack.pop();
			// Check the available size, depending on the direction
			Room room = attemptRoom(frame.x, frame.y, frame.generation, frame.direction);
			if (room != null) {
				if (frame.originPoint != null) {
					// Add props
					addDoor(frame, room);
				}
				// Pick each connection point and attempt to place a room
				room.connectionPoints.stream().filter(point -> !point.visited).forEach(point -> {
					// Attempt to generate a room in that direction (at a random separation)
					int roomSeparation = Rand.between(minRoomSeparation, maxRoomSeparation);
					stack.push(new Frame((int) point.coords.x + point.direction.x * roomSeparation, (int) point.coords.y + point.direction.y * roomSeparation, point.direction, point, roomSeparation, frame.generation + 1));
				});
				if (frame.originPoint != null) {
					frame.originPoint.active = true;
					// Make the connecting tile walkable
					for (int i = 0; i <= frame.roomSeparation; ++i) {
						tiles[(int) frame.originPoint.coords.x + frame.originPoint.direction.x * i][(int) frame.originPoint.coords.y + frame.originPoint.direction.y * i] = TileType.FLOOR;
					}
					frame.originPoint.visited = true;
				}
			}
		}
	}

	private void addDoor(Frame frame, Room room) {
		String doorType = frame.direction == Direction.UP || frame.direction == Direction.DOWN ? EntityType.DOOR_VERTICAL : EntityType.DOOR_HORIZONTAL;
		EntityPlaceholder door = new EntityPlaceholder(doorType, new Vector2(frame.originPoint.coords.x + 0.5f, frame.originPoint.coords.y));
		room.placeholders.add(door);
		door = new EntityPlaceholder(doorType, new Vector2(frame.originPoint.coords.x + 0.5f + frame.roomSeparation * frame.direction.x, frame.originPoint.coords.y + frame.roomSeparation * frame.direction.y));
		room.placeholders.add(door);
	}

	private Room attemptRoom(int x, int y, int generation, Direction direction) {
		if (x < 2 || x >= width - 2 || y < 2 || y >= height - 2) {
			return null;
		}

		// Attempt to place each room (in random order) until one succeeds
		Collections.shuffle(environment.getRooms());
		for (RoomPrototype prototype : environment.getRooms()) {
			Optional<ConnectionPoint> entryPoint = prototype.getConnections().stream().filter(d -> d.direction == direction.opposite()).findFirst();
			if (entryPoint.isPresent()) {
				ConnectionPoint p = entryPoint.get();
				Room room = new Room((int) (x - p.coords.x), (int) (y - p.coords.y), generation + 1, prototype);
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
		return true;
	}

	private void placeRoom(Room room) {
		rooms.add(room);
		for (int x = 0; x < room.width; ++x) {
			if (room.height >= 0)
				System.arraycopy(room.tiles[x], 0, tiles[x + room.left], room.bottom, room.height);
		}
	}

}
