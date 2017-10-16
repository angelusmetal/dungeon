package com.dungeon.character;

import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public class Projectile extends Entity<Projectile.AnimationType> implements Movable, Drawable {

	public enum AnimationType {
		FLY, EXPLOSION
	}

	private AnimationProvider<AnimationType> animationProvider;
	private float timeToLive;
	private float startTime;
	private boolean exploding = false;

	public Projectile(GameState state, float timeToLive, float startTime) {
		// TODO This should be moved to concrete subclasses...
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.FLY, state.getTilesetManager().getProjectileTileset().PROJECTILE_FLY_ANIMATION);
		provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_EXPLODE_ANIMATION);
		animationProvider = provider;
		setCurrentAnimation(provider.get(AnimationType.FLY));
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
				// Detect collision against
				for (Entity<?> entity : state.getEntities()) {
					if (entity != this && entity.isSolid() && entity.collides(getPos())) {
						explode(state);
					}
				}

			}
		}
	}

	@Override
	public boolean invertX() {
		return getSelfMovement().x < 0;
	}

	@Override
	protected void onSelfMovementUpdate() {
		// TODO Do we need this??
	}

	private void explode(GameState state) {
		exploding = true;
		setSelfXMovement(0);
		setSelfYMovement(0);
		setCurrentAnimation(animationProvider.get(AnimationType.EXPLOSION));
		startTime = state.getStateTime();
		timeToLive = 0.5f;
	}

}
