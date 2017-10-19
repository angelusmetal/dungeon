package com.dungeon.projectile;

import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.character.Entity;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public abstract class BaseProjectile extends Entity<BaseProjectile.AnimationType> implements Movable, Drawable {

	public enum AnimationType {
		FLY, EXPLOSION
	}

	protected AnimationProvider<AnimationType> animationProvider;
	protected float timeToLive;
	protected float startTime;
	protected boolean exploding = false;
	protected final int dmg;

	public BaseProjectile(float timeToLive, float startTime, int dmg) {
		this.timeToLive = timeToLive;
		this.startTime = startTime;
		this.dmg = dmg;
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
		// TODO Maybe the level should tell us what its tileset is?
		Tileset tileset = state.getTilesetManager().getDungeonTilesetDark();

		// Only if not already exploding
		if (!exploding) {
			getPos().add(getSelfMovement());

			// Detect collision against walls
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
	protected void onEntityCollision(GameState state, Entity<?> entity) {
		explode(state);
		entity.hit(state, dmg);
	}

	@Override
	public boolean invertX() {
		return getSelfMovement().x < 0;
	}

	@Override
	protected void onSelfMovementUpdate() {
		// TODO Do we need this??
	}

	protected void explode(GameState state) {
		exploding = true;
		setSelfXMovement(0);
		setSelfYMovement(0);
		setCurrentAnimation(animationProvider.get(AnimationType.EXPLOSION));
		startTime = state.getStateTime();
		timeToLive = 0.5f;
	}

}
