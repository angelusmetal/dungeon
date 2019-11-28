package com.dungeon.game.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
	TilePrototype prototype;
	Animation<TextureRegion> floorAnimation;
	Animation<TextureRegion> wallAnimation;
	boolean discovered = false;

	public Tile(TilePrototype prototype) {
		this.prototype = prototype;
	}

	public TilePrototype getPrototype() {
		return prototype;
	}

	public void setPrototype(TilePrototype prototype) {
		this.prototype = prototype;
	}

	public Animation<TextureRegion> getFloorAnimation() {
		return floorAnimation;
	}

	public void setFloorAnimation(Animation<TextureRegion> floorAnimation) {
		this.floorAnimation = floorAnimation;
	}

	public Animation<TextureRegion> getWallAnimation() {
		return wallAnimation;
	}

	public void setWallAnimation(Animation<TextureRegion> wallAnimation) {
		this.wallAnimation = wallAnimation;
	}

	public boolean isSolid() {
		return prototype.isSolid();
	}

	public boolean isDiscovered() {
		return discovered;
	}

	public void setDiscovered(boolean discovered) {
		this.discovered = discovered;
	}
}
