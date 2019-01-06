package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.physics.Body;

public class EntityMover {

	private static final Vector2 frameMovement = new Vector2();
	private static final Vector2 stepX = new Vector2();
	private static final Vector2 stepY = new Vector2();

	static public <T extends Entity> TraitSupplier<T> move() {
		return e -> EntityMover::move;
	}

	public static void move(Entity entity) {
		Vector2 movement = entity.getMovement();
		float speed = entity.getSpeed();

		// Update movement with self impulse
		float oldLength = movement.len();
		movement.add(entity.getSelfImpulse().x * speed, entity.getSelfImpulse().y * speed);
		float newLength = movement.len();

		// Even though an impulse can make the movement exceed the speed, selfImpulse should not help exceed it
		// (otherwise, it would accelerate indefinitely), but it can still help decrease it
		if (newLength > oldLength && newLength > speed) {
			float diff = newLength - speed;
			frameMovement.set(entity.getSelfImpulse()).setLength(diff);
			movement.sub(frameMovement);
		}

		frameMovement.set(movement).scl(Engine.frameTime());

		float distance = frameMovement.len();

		// Split into 1 px steps, and decompose in axes
		stepX.set(frameMovement).clamp(0,1);
		stepY.set(stepX);
		stepX.y = 0;
		stepY.x = 0;

		boolean collidedX = false;
		boolean collidedY = false;
		Body body = entity.getBody();
		while (distance > 1 && !(collidedX && collidedY)) {
			// do step
			if (!collidedX) {
				body.move(stepX);
				collidedX = detectEntityCollision(entity, stepX) && !entity.isNoclip();
			}
			if (!collidedX) {
				collidedX = detectTileCollision(entity, stepX) && !entity.isNoclip();
			}
			if (collidedX) {
				movement.x *= -entity.getBounciness();
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectEntityCollision(entity, stepY) && !entity.isNoclip();
			}
			if (!collidedY) {
				collidedY = detectTileCollision(entity, stepY) && !entity.isNoclip();
			}
			if (collidedY) {
				movement.y *= -entity.getBounciness();
			}
			distance -= 1;
		}
		if (distance > 0) {
			stepX.y *= distance;
			stepY.x *= distance;
			// do remainder
			if (!collidedX) {
				body.move(stepX);
				collidedX = detectTileCollision(entity, stepX) && !entity.isNoclip();
			}
			if (!collidedX) {
				detectEntityCollision(entity, stepX);
			}
			if (!collidedY) {
				body.move(stepY);
				collidedY = detectTileCollision(entity, stepY) && !entity.isNoclip();
			}
			if (!collidedY) {
				detectEntityCollision(entity, stepY);
			}
		}

		// Decrease speed
		movement.scl(1 / (1 + (Engine.frameTime() * entity.getFriction())));

		// Round out very small values
		if (Math.abs(movement.x) < 0.1f) {
			movement.x = 0;
		}
		if (Math.abs(movement.y) < 0.1f) {
			movement.y = 0;
		}

		// Handle vertical movement
		if (!entity.isExpired()) {
			float z = entity.getZPos();
			float zSpeed = entity.getZSpeed();
			z += zSpeed * Engine.frameTime();
			if (z < 0) {
				z = 0;
				if (entity.getBounciness() > 0 && Math.abs(zSpeed) > 10) {
					zSpeed *= -entity.getBounciness();
				} else {
					zSpeed = 0;
					entity.getOnRestTraits().forEach(m -> m.accept(entity));
					entity.onGroundRest();
				}
			}
			entity.setZPos(z);
			entity.setZSpeed(zSpeed);
		}
	}

	private static boolean detectTileCollision(Entity entity, Vector2 step) {
		int tile_size = Engine.getLevelTiles().getTileSize();
		Body body = entity.getBody();
		int left = body.getLeftTile(tile_size);
		int right = body.getRightTile(tile_size);
		int bottom = body.getBottomTile(tile_size);
		int top = body.getTopTile(tile_size);
		for (int x = left; x <= right; ++x) {
			for (int y = bottom; y <= top; ++y) {
				if (Engine.getLevelTiles().isSolid(x, y) && body.intersectsTile(x, y, tile_size) && !entity.isNoclip()) {
					body.move(step.scl(-1));
					entity.onTileCollision(Math.abs(step.x) > Math.abs(step.y));
					return true;
				}
			}
		}
		return false;
	}

	private static boolean detectEntityCollision(Entity entity, Vector2 step) {
		// Ugh...
		final boolean[] pushedBack = new boolean[1];
		Engine.entities.colliding(entity).forEach(other -> {
			if (other != entity) {
				// If this did not handle a collision with the other entity, have the other entity attempt to handle it
				if (!entity.onEntityCollision(other)) {
					other.onEntityCollision(entity);
				}
				// If collides with a solid entity, push back
				if (entity.isSolid() && !pushedBack[0] && other.isSolid()) {
					entity.getBody().move(step.scl(-1));
					pushedBack[0] = true;
				}
			}
		});
		return pushedBack[0];
	}

}
