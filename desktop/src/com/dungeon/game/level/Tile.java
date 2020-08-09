package com.dungeon.game.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Tile {
	TilePrototype prototype;
	Animation<Sprite> floorAnimation;
	Animation<Sprite> wallAnimation;
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

	public Animation<Sprite> getFloorAnimation() {
		return floorAnimation;
	}

	public void setFloorAnimation(Animation<Sprite> floorAnimation) {
		this.floorAnimation = floorAnimation;
	}

	public Animation<Sprite> getWallAnimation() {
		return wallAnimation;
	}

	public void setWallAnimation(Animation<Sprite> wallAnimation) {
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
