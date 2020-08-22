package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.dungeon.engine.render.Material;

public abstract class Tileset {

	public abstract Animation<Material> down();
	public abstract Animation<Material> left();
	public abstract Animation<Material> up();
	public abstract Animation<Material> right();
	public abstract Animation<Material> downRight();
	public abstract Animation<Material> downLeft();
	public abstract Animation<Material> upLeft();
	public abstract Animation<Material> upRight();
	public abstract Animation<Material> upDown();
	public abstract Animation<Material> leftRight();
	public abstract Animation<Material> downLeftRight();
	public abstract Animation<Material> upDownLeft();
	public abstract Animation<Material> upLeftRight();
	public abstract Animation<Material> upDownRight();
	public abstract Animation<Material> all();
	public abstract Animation<Material> cornerA();
	public abstract Animation<Material> cornerB();
	public abstract Animation<Material> cornerAB();
	public abstract Animation<Material> cornerC();
	public abstract Animation<Material> cornerAC();
	public abstract Animation<Material> cornerBC();
	public abstract Animation<Material> cornerABC();
	public abstract Animation<Material> cornerD();
	public abstract Animation<Material> cornerAD();
	public abstract Animation<Material> cornerBD();
	public abstract Animation<Material> cornerABD();
	public abstract Animation<Material> cornerCD();
	public abstract Animation<Material> cornerACD();
	public abstract Animation<Material> cornerBCD();
	public abstract Animation<Material> cornerABCD();
	public abstract Animation<Material> none();
	public abstract Animation<Material> cornerABRight();
	public abstract Animation<Material> cornerBCDown();
	public abstract Animation<Material> cornerCDLeft();
	public abstract Animation<Material> cornerADUp();
	public abstract Animation<Material> cornerARight();
	public abstract Animation<Material> cornerBRight();
	public abstract Animation<Material> cornerBDown();
	public abstract Animation<Material> cornerCDown();
	public abstract Animation<Material> cornerDLeft();
	public abstract Animation<Material> cornerCLeft();
	public abstract Animation<Material> cornerAUp();
	public abstract Animation<Material> cornerDUp();
	public abstract Animation<Material> cornerBDownRight();
	public abstract Animation<Material> cornerCDownLeft();
	public abstract Animation<Material> cornerDUpLeft();
	public abstract Animation<Material> cornerAUpRight();
}
