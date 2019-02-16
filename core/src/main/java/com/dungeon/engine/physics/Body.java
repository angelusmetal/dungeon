package com.dungeon.engine.physics;

import com.badlogic.gdx.math.Vector2;

public class Body {
	private final Vector2 origin;
	private final Vector2 boundingBox;
	private final Vector2 bottomLeft;
	private final Vector2 topRight;

	private Body(Vector2 origin, Vector2 boundingBox, Vector2 bottomLeft, Vector2 topRight) {
		this.origin = origin;
		this.boundingBox = boundingBox;
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
	}

	public static Body centered(Vector2 origin, Vector2 boundingBox) {
		Vector2 newOrigin = origin.cpy();
		Vector2 newBoundingBox = boundingBox.cpy();
		Vector2 newBottomLeft = origin.cpy();
		newBottomLeft.x -= boundingBox.x / 2;
		newBottomLeft.y -= boundingBox.y / 2;
		Vector2 newTopRight = newBottomLeft.cpy().add(boundingBox);
		return new Body(newOrigin, newBoundingBox, newBottomLeft, newTopRight);
	}

	public static Body withOffset(Vector2 origin, Vector2 boundingBox, Vector2 offset) {
		Vector2 newOrigin = origin.cpy();
		Vector2 newBoundingBox = boundingBox.cpy();
		Vector2 newBottomLeft = origin.cpy().sub(offset);
		Vector2 newTopRight = newBottomLeft.cpy().add(boundingBox);
		return new Body(newOrigin, newBoundingBox, newBottomLeft, newTopRight);
	}

	public void move(Vector2 movement) {
		origin.add(movement);
		bottomLeft.add(movement);
		topRight.add(movement);
	}

	public boolean intersects(Vector2 point) {
		return bottomLeft.x <= point.x && topRight.x >= point.x
				&& bottomLeft.y <= point.y && topRight.y >= point.y;
	}

	public boolean intersects(Body other) {
		return bottomLeft.x <= other.topRight.x && topRight.x >= other.bottomLeft.x
				&& bottomLeft.y <= other.topRight.y && topRight.y >= other.bottomLeft.y;
	}

	public boolean intersects(float left, float right, float bottom, float top) {
		return bottomLeft.x <= right && topRight.x >= left
				&& bottomLeft.y <= top && topRight.y >= bottom;
	}

	public boolean intersectsTile(int x, int y, int tile_size) {
		int left = x * tile_size;
		int right = left + tile_size;
		int bottom = y * tile_size;
		int top = bottom + tile_size;
		return bottomLeft.x <= right && topRight.x >= left
				&& bottomLeft.y <= top && topRight.y >= bottom;
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public Vector2 getBoundingBox() {
		return boundingBox;
	}

	public Vector2 getBottomLeft() {
		return bottomLeft;
	}

	public Vector2 getTopRight() {
		return topRight;
	}

	public int getLeftTile(int tile_size) {
		return (int) (bottomLeft.x / tile_size);
	}

	public int getRightTile(int tile_size) {
		return (int) (topRight.x / tile_size);
	}

	public int getBottomTile(int tile_size) {
		return (int) (bottomLeft.y / tile_size);
	}

	public int getTopTile(int tile_size) {
		return (int) (topRight.y / tile_size);
	}

	public Vector2 getCenter() {
		return new Vector2((bottomLeft.x + topRight.x) / 2, (bottomLeft.y + topRight.y) / 2);
	}
}
