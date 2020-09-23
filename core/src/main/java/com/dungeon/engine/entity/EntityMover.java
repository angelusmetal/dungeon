package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.physics.Body;

/**
 * Moves entities and handles collision against canBlock tiles and entities
 */
public class EntityMover {

	private static final Vector2 frameMovement = new Vector2();
	private static final Vector2 stepX = new Vector2();
	private static final Vector2 stepY = new Vector2();

	public static <T extends Entity> TraitSupplier<T> move() {
		return e -> EntityMover::move;
	}

	public static void move(Entity entity) {
		Vector2 movement = entity.getMovement();
		float speed = entity.getSpeed();

		// Update movement with self impulse
		float maxSpeed = Math.max(movement.len(), speed);
		movement.add(entity.getEffectiveSelfImpulse().x * speed, entity.getEffectiveSelfImpulse().y * speed);

		// Decrease speed due to friction
		float frameFriction;
		if (entity.getZPos() <= 0) {
			frameFriction = entity.getFriction() * Engine.frameTime();
		} else {
			frameFriction = entity.getAirFriction() * Engine.frameTime();
		}
		if (frameFriction > 1f) {
			frameFriction = 1f;
		}
		movement.x -= movement.x * frameFriction;
		movement.y -= movement.y * frameFriction;
		maxSpeed -= maxSpeed * frameFriction;

		if (movement.len2() < 0.01) {
			movement.set(0f, 0f);
		} else {
			// Even though an impulse can make the movement exceed the speed, selfImpulse should not help exceed it
			// (otherwise, it would accelerate indefinitely), but it can still help decrease it
			movement.clamp(0f, maxSpeed);

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
					collidedX = detectEntityCollision(entity, stepX);
				}
				if (!collidedX) {
					collidedX = entity.canBeBlockedByTiles() && detectTileCollision(entity, stepX);
				}
				if (collidedX) {
					movement.x *= -entity.getBounciness();
				}
				if (!collidedY) {
					body.move(stepY);
					collidedY = detectEntityCollision(entity, stepY);
				}
				if (!collidedY) {
					collidedY = entity.canBeBlockedByTiles() && detectTileCollision(entity, stepY);
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
					collidedX = entity.canBeBlockedByTiles() && detectTileCollision(entity, stepX);
				}
				if (!collidedX) {
					detectEntityCollision(entity, stepX);
				}
				if (!collidedY) {
					body.move(stepY);
					collidedY = entity.canBeBlockedByTiles() && detectTileCollision(entity, stepY);
				}
				if (!collidedY) {
					detectEntityCollision(entity, stepY);
				}
			}

			// Round out very small values
			if (Math.abs(movement.x) < 0.1f) {
				movement.x = 0;
			}
			if (Math.abs(movement.y) < 0.1f) {
				movement.y = 0;
			}

			if (collidedX || collidedY) {
				// Notify that movement has been updated
				entity.onMovementUpdate();
			}

		}

		// Handle vertical movement
		if (!entity.isExpired()) {
			float z = entity.getZPos();
			float zSpeed = entity.getZSpeed();
			z += zSpeed * Engine.frameTime();
			if (z < 0) {
				z = 0;
				// If entity was above ground, call onGroundHit()
				if (entity.getZPos() > 0) {
					entity.getOnGroundHitTraits().forEach(m -> m.accept(entity));
					entity.onGroundHit();
				}
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
		// Iterate from bottom left to upper right intersecting tiles and negate movement if any of them are solid
		int tileSize = Engine.getLevelTiles().getTileSize();
		Body body = entity.getBody();
		for (int x = body.getLeftTile(tileSize); x <= body.getRightTile(tileSize); ++x) {
			for (int y = body.getBottomTile(tileSize); y <= body.getTopTile(tileSize); ++y) {
				if (Engine.getLevelTiles().isSolid(x, y) && body.intersectsTile(x, y, tileSize)) {
					entity.onTileCollision(Math.abs(step.x) > Math.abs(step.y));
					body.move(step.scl(-1));
					return true;
				}
			}
		}
		return false;
	}

	private static boolean detectEntityCollision(Entity entity, Vector2 step) {
		// Ugh...
		final boolean[] pushedBack = new boolean[1];
		Engine.entities.colliding(entity).filter(e -> e != entity).forEach(other -> {
			// If this did not handle a collision with the other entity, have the other entity attempt to handle it
			if (!entity.onEntityCollision(other)) {
				other.onEntityCollision(entity);
			}
			// If collides with an entity that can block (and this can be blocked by entities) push back
			if (entity.canBeBlockedByEntities() && !pushedBack[0] && other.canBlock()) {
				entity.getBody().move(step.scl(-1));
				pushedBack[0] = true;
			}
		});
		return pushedBack[0];
	}

}
