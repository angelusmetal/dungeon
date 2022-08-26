package com.dungeon.game.level;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.dungeon.engine.render.Material;

public class Tile {
	TilePrototype prototype;
	Animation<Material> floorAnimation;
	Animation<Material> wallAnimation;
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

	public Animation<Material> getFloorAnimation() {
		return floorAnimation;
	}

	public void setFloorAnimation(Animation<Material> floorAnimation) {
		this.floorAnimation = floorAnimation;
	}

	public Animation<Material> getWallAnimation() {
		return wallAnimation;
	}

	public void setWallAnimation(Animation<Material> wallAnimation) {
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
