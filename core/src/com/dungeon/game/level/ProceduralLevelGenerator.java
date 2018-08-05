package com.dungeon.game.level;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Tile;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityPlaceholder;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.tileset.Tileset;
import com.moandjiezana.toml.Toml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class ProceduralLevelGenerator {

	public static final List<String> DEFAULT_MONSTER_TYPES = Arrays.asList("GHOST", "SLIME", "SLIME_ACID", "SLIME_FIRE");

	private int width;
	private int height;
	private final Toml configuration;
	private TileType[][] tiles;
	private int maxRoomSeparation = 8;
	private int minRoomSeparation = 2;
	private List<Room> rooms = new ArrayList<>();
	private List<Coords> doors = new ArrayList<>();

	private List<RoomPrototype> roomPrototypes = new ArrayList<>();

	public enum Type {
		HEART,
		NORMAL
	}

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

	public static class Coords {
		public int x, y;
		public Coords(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "x: " + x + ", y: " + y;
		}
	}

	public static class ConnectionPoint {
		Coords coords;
		Direction direction;
		boolean visited = false;
		boolean active = false;
		public ConnectionPoint (int x, int y, Direction direction) {
			this.coords = new Coords(x, y);
			this.direction = direction;
		}

		@Override
		public String toString() {
			return "coords: [" + coords + "], visited: " + visited + ", active: " + active;
		}
	}

	public ProceduralLevelGenerator(Toml configuration, int width, int height) {
		this.configuration = configuration;
		this.width = width;
		this.height = height;
		this.tiles = new TileType[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = TileType.VOID;
			}
		}
		Toml toml = new Toml().read(ResourceManager.class.getClassLoader().getResourceAsStream("rooms.toml"));
		toml.entrySet().forEach(entry -> {
			roomPrototypes.add(RoomPrototype.fromToml(toml.getTable(entry.getKey())));
		});

	}

	public Level generateLevel(Tileset tileset) {
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
			generateRooms(startX, startY, direction, Type.HEART);
		}

		Tile[][] map = new Tile[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				map[x][y] = getTile(x, y, tileset);
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
		List<EntityType> monsterTypes = configuration.getList("map.creatures", DEFAULT_MONSTER_TYPES).stream().map(EntityType::valueOf).collect(Collectors.toList());
		List<EntityPlaceholder> placeholders = new ArrayList<>();

		// Create monsters in each room, to begin with
		for (int r = 1; r < rooms.size(); ++r) {
			Room room = rooms.get(r);
			// Create a random amount of monsters in each room
			final int skip = Rand.nextInt(room.spawnPoints.size());

			room.spawnPoints.forEach(pos -> placeholders.add(new EntityPlaceholder(Rand.pick(monsterTypes), pos)));

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
		final Type type;
		final ConnectionPoint originPoint;
		final int roomSeparation;
		final int generation;
		public Frame(int x, int y, Direction direction, Type type, ConnectionPoint originPoint, int roomSeparation, int generation) {
			this.x = x;
			this.y = y;
			this.direction = direction;
			this.type = type;
			this.originPoint = originPoint;
			this.roomSeparation = roomSeparation;
			this.generation = generation;
		}
	}

	private void generateRooms(int x, int y, Direction direction, Type type) {
		Stack<Frame> stack = new Stack<>();
		stack.push(new Frame(x, y, direction, type, null, 0, 1));
		while (!stack.isEmpty()) {
			Frame frame = stack.pop();
			System.out.println("Generating " + frame.type + " room at " + frame.x + ", " + frame.y + ", with direction " + frame.direction);
			// Check the available size, depending on the direction
			Room room = attemptRoom(frame.x, frame.y, frame.generation, frame.direction);
			if (room != null) {
				if (frame.originPoint != null) {
					// Add props
					addDoor(frame, room);
				}
				// Pick each connection point and attempt to place a room
				room.connectionPoints.stream().filter(point -> !point.visited).forEach(point -> {
					System.out.println("   -> Visiting connection point " + point + "...");
					// Attempt to generate a room in that direction (at a random separation)
					int roomSeparation = Rand.between(minRoomSeparation, maxRoomSeparation);
					stack.push(new Frame(point.coords.x + point.direction.x * roomSeparation, point.coords.y + point.direction.y * roomSeparation, point.direction, Type.NORMAL, point, roomSeparation, frame.generation + 1));
				});
				if (frame.originPoint != null) {
					frame.originPoint.active = true;
					// Make the connecting tile walkable
					for (int i = 0; i <= frame.roomSeparation; ++i) {
						tiles[frame.originPoint.coords.x + frame.originPoint.direction.x * i][frame.originPoint.coords.y + frame.originPoint.direction.y * i] = TileType.FLOOR;
						// TODO Hmm not entirely right... I need to fix this
						doors.add(new Coords(frame.originPoint.coords.x + frame.originPoint.direction.x,frame.originPoint.coords.y + frame.originPoint.direction.y));
					}
					frame.originPoint.visited = true;
				}
			} else {
				System.out.println("... but could not place it");
			}
		}
	}

	private void addDoor(Frame frame, Room room) {
		EntityType doorType = frame.direction == Direction.UP || frame.direction == Direction.DOWN ? EntityType.DOOR_VERTICAL : EntityType.DOOR_HORIZONTAL;
		// TODO replace Coords with Vector2...
		EntityPlaceholder door = new EntityPlaceholder(doorType, new Vector2(frame.originPoint.coords.x + 0.5f, frame.originPoint.coords.y + 0.5f));
		room.placeholders.add(door);
		door = new EntityPlaceholder(doorType, new Vector2(frame.originPoint.coords.x + 0.5f + frame.roomSeparation * frame.direction.x, frame.originPoint.coords.y + 0.5f + frame.roomSeparation * frame.direction.y));
		room.placeholders.add(door);
	}

	private Room attemptRoom(int x, int y, int generation, Direction direction) {
		if (x < 2 || x >= width - 2 || y < 2 || y >= height - 2) {
			return null;
		}

		// Attempt to place each room (in random order) until one succeeds
		Collections.shuffle(roomPrototypes);
		for (RoomPrototype prototype : roomPrototypes) {
			Optional<ConnectionPoint> entryPoint = prototype.getConnections().stream().filter(d -> d.direction == direction.opposite()).findFirst();
			if (entryPoint.isPresent()) {
				ConnectionPoint p = entryPoint.get();
				Room room = new Room(x - p.coords.x, y - p.coords.y, generation + 1, prototype);
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
		System.out.println("Placing room " + room);
		rooms.add(room);
		for (int x = 0; x < room.width; ++x) {
			for (int y = 0; y < room.height; ++y) {
				tiles[x + room.left][y + room.bottom] = room.tiles[x][y];
			}
		}
	}

}
