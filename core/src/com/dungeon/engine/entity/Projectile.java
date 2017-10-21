package com.dungeon.engine.entity;

import com.dungeon.engine.render.Drawable;
import com.dungeon.game.GameState;
import com.dungeon.engine.animation.AnimationProvider;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Tileset;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends Entity<Projectile.AnimationType> implements Movable, Drawable {

	/**
	 * Describes animation types for projectiles
	 */
	public enum AnimationType {
		FLY_NORTH, FLY_SOUTH, FLY_SIDE, EXPLOSION
	}

	protected AnimationProvider<AnimationType> animationProvider;
	protected float timeToLive;
	protected float startTime;
	protected boolean exploding = false;

	public Projectile(float timeToLive, float startTime) {
		this.timeToLive = timeToLive;
		this.startTime = startTime;
	}

	@Override
	public boolean isExpired(float time) {
		return (startTime + timeToLive) < time;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public void move(GameState state) {
		// Only if not already exploding
		if (!exploding) {
			// Move projectile in the current direction
			getPos().add(getSelfMovement());

			// Detect collision against walls
			Tileset tileset = state.getLevelTileset();
			int xTile = (int)getPos().x / tileset.tile_width;
			int yTile = (int)getPos().y / tileset.tile_height;
			if (!state.getLevel().walkableTiles[xTile][yTile]) {
				explode(state);
			} else {
				// Detect collision against entities
				for (Entity<?> entity : state.getEntities()) {
					if (entity != this && entity.isSolid() && entity.collides(getPos())) {
						onEntityCollision(state, entity);
					}
				}

			}
		}
	}

	@Override
	public void onSelfMovementUpdate() {
		// Updates current animation based on the direction vector
		AnimationType animationType;
		if (Math.abs(getSelfMovement().x) > Math.abs(getSelfMovement().y)) {
			// Sideways animation; negative values invert X
			animationType = AnimationType.FLY_SIDE;
			setInvertX(getSelfMovement().x < 0);
		} else {
			// North / south animation
			animationType = getSelfMovement().y < 0 ? AnimationType.FLY_SOUTH : AnimationType.FLY_NORTH;
		}
		setCurrentAnimation(animationProvider.get(animationType));

	}

	/**
	 * Triggers the projectile explosion
	 */
	protected void explode(GameState state) {
		// Set exploding status and animation (and TTL to expire right after explosion end)
		exploding = true;
		setCurrentAnimation(animationProvider.get(AnimationType.EXPLOSION));
		startTime = state.getStateTime();
		timeToLive = getCurrentAnimation().getDuration();
	}

}
