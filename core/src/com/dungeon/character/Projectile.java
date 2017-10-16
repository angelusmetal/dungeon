package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.Drawable;
import com.dungeon.GameState;
import com.dungeon.animation.AnimationProvider;
import com.dungeon.animation.GameAnimation;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public class Projectile extends Entity<Projectile.AnimationType> implements Movable, Drawable {

	public enum AnimationType {
		BULLET, EXPLOSION
	}

	private AnimationProvider<AnimationType> animationProvider;
	private float timeToLive;
	private float startTime;

	public Projectile(GameState state, float timeToLive, float startTime) {
		// TODO This should be moved to concrete subclasses...
		AnimationProvider<AnimationType> provider = new AnimationProvider<>(AnimationType.class, state);
		provider.register(AnimationType.BULLET, state.getTilesetManager().getProjectileTileset().PROJECTILE_ANIMATION);
		provider.register(AnimationType.EXPLOSION, state.getTilesetManager().getProjectileTileset().PROJECTILE_ANIMATION);
		animationProvider = provider;
		setCurrentAnimation(provider.get(AnimationType.BULLET));
		this.timeToLive = timeToLive;
		this.startTime = startTime;
	}

	@Override
	public boolean isExpired(float time) {
		return (startTime + timeToLive) < time;
	}

	@Override
	public void move(GameState state) {
		// TODO Maybe the level should tell us what its tileset is?
		Tileset tileset = state.getTilesetManager().getDungeonTilesetDark();

		getPos().add(getSelfMovement());
		// Collision detection!
		int xTile = (int)getPos().x / tileset.tile_width;
		int yTile = (int)getPos().y / tileset.tile_height;
		if (!state.getLevel().walkableTiles[xTile][yTile]) {
			getPos().sub(getSelfMovement());
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

}
