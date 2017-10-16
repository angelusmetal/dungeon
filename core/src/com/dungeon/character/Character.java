package com.dungeon.character;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.Drawable;
import com.dungeon.Dungeon;
import com.dungeon.animation.GameAnimation;
import com.dungeon.level.Level;
import com.dungeon.movement.Movable;
import com.dungeon.tileset.Tileset;

public class Character implements Movable, Drawable {
	private CharacterAnimationProvider animationProvider;
	private GameAnimation<TextureRegion> currentAnimation;
	private final Vector2 pos = new Vector2();
	private final Vector2 drawOffset = new Vector2();
	private final Vector2 selfMovement = new Vector2();
	private final Vector2 movementSpeed = new Vector2();
	protected float maxMovementSpeed = 3;
	private boolean invertX = false;

	public Character(CharacterAnimationProvider animationProvider) {
		this.animationProvider = animationProvider;
		this.currentAnimation = animationProvider.getIdle();
		this.drawOffset.set(getFrame(0).getRegionWidth() / 2, getFrame(0).getRegionHeight() / 2);
	}

	public Character(CharacterAnimationProvider animationProvider, Vector2 drawOffset) {
		this.animationProvider = animationProvider;
		this.currentAnimation = animationProvider.getIdle();
		this.drawOffset.set(drawOffset);
	}

	@Override
	public Vector2 getPos() {
		return pos;
	}

	@Override
	public Vector2 getDrawOffset() {
		return drawOffset;
	}

	public void setPos(Vector2 pos) {
		this.pos.set(pos);
	}

	@Override
	public void setSelfXMovement(float x) {
		selfMovement.x = x;
		checkMovementAnimation();
	}

	@Override
	public void setSelfYMovement(float y) {
		selfMovement.y = y;
		checkMovementAnimation();
	}

	public void setSelfMovement(Vector2 vector) {
		selfMovement.set(vector);
		checkMovementAnimation();
	}

	private void checkMovementAnimation() {
		if (selfMovement.x != 0) {
			invertX = selfMovement.x < 0;
		}
		if (selfMovement.x == 0 && selfMovement.y == 0) {
			if (!"idle".equals(currentAnimation.getId())) {
				currentAnimation = animationProvider.getIdle();
			}
		} else {
			if (!"walk".equals(currentAnimation.getId())) {
				currentAnimation = animationProvider.getWalk();
			}
		}
	}

	public Vector2 getSelfMovement() {
		return selfMovement;
	}

	public Vector2 getMovementSpeed() {
		return movementSpeed;
	}

	public void move(Level level, Tileset tileset) {
		// Update movementSpeed
		movementSpeed.add(selfMovement);
		movementSpeed.clamp(0, maxMovementSpeed);

		// Apply movement and detect collision
		int prevXTile = (int)pos.x / tileset.tile_width;
		int prevYTile = (int)pos.y / tileset.tile_height;
		pos.add(movementSpeed);
		int xTile = (int)pos.x / tileset.tile_width;
		int yTile = (int)pos.y / tileset.tile_height;
		if (prevXTile != xTile && !level.walkableTiles[xTile][prevYTile]) {
			pos.x -= movementSpeed.x;
		}
		if (prevYTile != yTile && !level.walkableTiles[prevXTile][yTile]) {
			pos.y -= movementSpeed.y;
		}

		// Decrease speed
		movementSpeed.scl(0.9f);
	}

	public TextureRegion getFrame(float stateTime) {
		return currentAnimation.getKeyFrame(stateTime);
	}

	public boolean invertX() {
		return invertX;
	}

	public void fire(Dungeon dungeon) {
		Projectile projectile = new Projectile(dungeon.tilesetManager.getProjectileTileset().PROJECTILE_ANIMATION, 10, dungeon.stateTime);
		projectile.setPos(pos);
		projectile.setSelfMovement(selfMovement);
		float len = projectile.getSelfMovement().len();
		projectile.getSelfMovement().scl(5 / len);
		dungeon.projectiles.add(projectile);
		GameAnimation<TextureRegion> oldAnimation = currentAnimation;
		currentAnimation = animationProvider.getHit(this::checkMovementAnimation);
		System.out.println("FIRE!");
	}
}
