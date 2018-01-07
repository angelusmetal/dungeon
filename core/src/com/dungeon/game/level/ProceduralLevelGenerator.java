package com.dungeon.game.level;

import com.dungeon.engine.render.Tile;
import com.dungeon.game.tileset.DungeonVioletTileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProceduralLevelGenerator {

	// TODO streamline random methods to reduce boilerplate code and make them based on seeds

	private int width;
	private int height;
	// Bit map of walkable tiles
	private boolean[][] walkableTiles;
	private int maxRoomSize = 12;
	private int minRoomSize = 3;
	private int maxRoomSeparation = 8;
	private int minRoomSeparation = 2;
	private List<Room> rooms = new ArrayList<>();
	private List<Coords> doors = new ArrayList<>();

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

	public ProceduralLevelGenerator(int width, int height) {
		this.width = width;
		this.height = height;
		this.walkableTiles = new boolean[width][height];
	}

	public Level generateLevel(DungeonVioletTileset tileset) {
		// Pick a random position to start (excluding border rows/columns)
		int startX = (int) (Math.random() * (width - 10)) + 5;
		int startY = (int) (Math.random() * (height - 10)) + 5;

		// Depending on the closest edge (bottom, left, right, up) pick the opposite edge as search direction to grow the room
		Direction direction;
		int closestEdgeX = Math.min(startX, width - startX);
		int closestEdgeY = Math.min(startY, height - startY);
		if (closestEdgeX < closestEdgeY) {
			direction = closestEdgeX == startX ? Direction.RIGHT : Direction.LEFT;
		} else {
			direction = closestEdgeY == startY ? Direction.UP : Direction.DOWN;
		}
		// Generate the root (heart) room, and the rest from there
		generateRoom(startX, startY, direction, Type.HEART);

		Tile[][] map = new Tile[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				map[x][y] = getTile(x, y, tileset);
			}
		}

		Level level = new Level();
		level.map = map;
		level.rooms = rooms;
		level.walkableTiles = walkableTiles;
		return level;
	}

	private Tile getTile(int x, int y, DungeonVioletTileset tileset) {

		if (walkableTiles[x][y]) {
			// Return a random floor tile
			return tileset.FLOOR_TILES[(int) (Math.random() * tileset.FLOOR_TILES.length)];
		}

		boolean freeUp = y > 0 && walkableTiles[x][y-1];
		boolean freeDown = y < height - 1 && walkableTiles[x][y+1];
		boolean freeLeft = x > 0 && walkableTiles[x-1][y];
		boolean freeRight = x < width - 1 && walkableTiles[x+1][y];
		boolean freeUpLeft = y > 0 && x > 0 && walkableTiles[x-1][y-1];
		boolean freeUpRight = y > 0 && x < width - 1 && walkableTiles[x+1][y-1];
		boolean freeDownLeft = y < height - 1 && x > 0 && walkableTiles[x-1][y+1];
		boolean freeDownRight = y < height - 1 && x < width - 1 && walkableTiles[x+1][y+1];

		if (freeUp) {
			if (freeLeft) {
				return tileset.CONVEX_UPPER_RIGHT_TILE;
			} else if (freeRight) {
				return tileset.CONVEX_UPPER_LEFT_TILE;
			} else {
				return tileset.CONCAVE_UPPER_TILE;
			}
		} else if (freeLeft) {
			if (freeDown) {
				return tileset.CONVEX_LOWER_RIGHT_TILE;
			} else {
				return tileset.CONCAVE_RIGHT_TILE;
			}
		} else if (freeDown) {
			if (freeRight) {
				return tileset.CONVEX_LOWER_LEFT_TILE;
			} else {
				return tileset.CONCAVE_LOWER_TILE;
			}
		} else if (freeRight) {
			return tileset.CONCAVE_LEFT_TILE;
		} else if (freeUpLeft) {
			return tileset.CONCAVE_UPPER_RIGHT_TILE;
		} else if (freeUpRight) {
			return tileset.CONCAVE_UPPER_LEFT_TILE;
		} else if (freeDownLeft) {
			return tileset.CONCAVE_LOWER_RIGHT_TILE;
		} else if (freeDownRight) {
			return tileset.CONCAVE_LOWER_LEFT_TILE;
		}
		return tileset.VOID_TILE;
	}

	private Room generateRoom(int x, int y, Direction direction, Type type) {
		System.out.println("Generating " + type + " room...");
		// Pick the room size
		int roomWidth = (int) (Math.random() * (maxRoomSize - minRoomSize) + minRoomSize);
		int roomHeight = (int) (Math.random() * (maxRoomSize - minRoomSize) + minRoomSize);
		// Check the available size, depending on the direction
		Room room = attemptRoom(x, y, direction, roomWidth, roomHeight);
		if (room != null) {
			System.out.println("   -> x: " + x + ", y: " + y + ", width: " + roomWidth + ", height: " + roomHeight);
			// Pick each connection point and attempt to place a room
			room.connectionPoints.stream().filter(point -> !point.visited).forEach(point -> {
				System.out.println("   -> Visiting connection point " + point + "...");
				// Attempt to generate a room in that direction (at a random separation)
				int roomSeparation = (int) (Math.random() * (maxRoomSeparation - minRoomSize) + minRoomSeparation);
				Room generated = generateRoom(point.coords.x + point.direction.x * roomSeparation, point.coords.y + point.direction.y * roomSeparation, point.direction, Type.NORMAL);
				if (generated != null) {
					point.active = true;
					// Make the connecting tile walkable
					for (int i = 1; i < roomSeparation; ++i) {
						walkableTiles[point.coords.x + point.direction.x * i][point.coords.y + point.direction.y * i] = true;
						// TODO Hmm not entirely right... I need to fix this
						doors.add(new Coords(point.coords.x + point.direction.x,point.coords.y + point.direction.y));
					}
				}
				point.visited = true;
			});
		}
		return room;
	}

	private Room attemptRoom(int x, int y, Direction direction, int roomWidth, int roomHeight) {
		if (x < 2 || x >= width - 2 || y < 2 || y >= height - 2) {
			return null;
		}
		for (int rWidth = roomWidth; rWidth >= minRoomSize; --rWidth) {
			for (int rHeight = roomHeight; rHeight >= minRoomSize; --rHeight) {
				Room room = makeRoomCoords(x, y, roomWidth, roomHeight, direction);
				if (canPlaceRoom(room)) {
					placeRoom(room);
					addConnectionPoints(room, direction);
					return room;
				}
			}
		}
		// Could not generate a room
		return null;
	}

	private Room makeRoomCoords(int x, int y, int width, int height, Direction direction) {
		Room room = new Room();
		if (direction == Direction.LEFT || direction == Direction.RIGHT) {
			room.bottomRight.y = y - height / 2;
			room.topLeft.y = room.bottomRight.y + height - 1;
			if (direction == Direction.LEFT) {
				room.bottomRight.x = x;
				room.topLeft.x = room.bottomRight.x - width + 1;
			} else {
				room.topLeft.x = x;
				room.bottomRight.x = room.topLeft.x + width - 1;
			}
		} else {
			room.topLeft.x = x - width / 2;
			room.bottomRight.x = room.topLeft.x + width - 1;
			if (direction == Direction.UP) {
				room.bottomRight.y = y;
				room.topLeft.y = room.bottomRight.y + height - 1;
			} else {
				room.topLeft.y = y;
				room.bottomRight.y = room.topLeft.y - height + 1;
			}
		}
		return room;
	}

	private boolean canPlaceRoom(Room room) {
		boolean canPlace = true;
		for (int x = room.topLeft.x-2; x <= room.bottomRight.x+2; ++x) {
			for (int y = room.bottomRight.y-2; y <= room.topLeft.y+2; ++y) {
				if (x >= width-2 || x < 2 || y >= height-2 || y < 2 || walkableTiles[x][y]) {
					canPlace = false;
				}
			}
		}
		return canPlace;
	}

	private void placeRoom(Room room) {
		rooms.add(room);
		for (int x = room.topLeft.x; x <= room.bottomRight.x; ++x) {
			for (int y = room.bottomRight.y; y <= room.topLeft.y; ++y) {
				walkableTiles[x][y] = true;
			}
		}
	}

	public void addConnectionPoints(Room room, final Direction enterDirection) {
		System.out.println("Adding connection points to room " + room + "...");
		int middleX = (room.topLeft.x + room.bottomRight.x) / 2;
		int middleY = (room.bottomRight.y + room.topLeft.y) / 2;
		room.connectionPoints = new ArrayList<>(4);
		// Top connection point
		ConnectionPoint point = new ConnectionPoint(middleX, room.topLeft.y, Direction.UP);
		if (enterDirection == Direction.DOWN) {
			point.visited = true;
		}
		System.out.println("  -> " + point);
		room.connectionPoints.add(point);
		// Bottom connection point
		point = new ConnectionPoint(middleX, room.bottomRight.y, Direction.DOWN);
		if (enterDirection == Direction.UP) {
			point.visited = true;
		}
		System.out.println("  -> " + point);
		room.connectionPoints.add(point);
		// Left connection point
		point = new ConnectionPoint(room.topLeft.x, middleY, Direction.LEFT);
		if (enterDirection == Direction.RIGHT) {
			point.visited = true;
		}
		System.out.println("  -> " + point);
		room.connectionPoints.add(point);
		// Right connection point
		point = new ConnectionPoint(room.bottomRight.x, middleY, Direction.RIGHT);
		if (enterDirection == Direction.LEFT) {
			point.visited = true;
		}
		System.out.println("  -> " + point);
		room.connectionPoints.add(point);
		// Shuffle connection points so they are not always navigated in the same order
		Collections.shuffle(room.connectionPoints);
	}
}
