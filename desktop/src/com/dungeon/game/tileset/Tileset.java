package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Tileset {

	public abstract Animation<Sprite> down();
	public abstract Animation<Sprite> left();
	public abstract Animation<Sprite> up();
	public abstract Animation<Sprite> right();
	public abstract Animation<Sprite> downRight();
	public abstract Animation<Sprite> downLeft();
	public abstract Animation<Sprite> upLeft();
	public abstract Animation<Sprite> upRight();
	public abstract Animation<Sprite> upDown();
	public abstract Animation<Sprite> leftRight();
	public abstract Animation<Sprite> downLeftRight();
	public abstract Animation<Sprite> upDownLeft();
	public abstract Animation<Sprite> upLeftRight();
	public abstract Animation<Sprite> upDownRight();
	public abstract Animation<Sprite> all();
	public abstract Animation<Sprite> cornerA();
	public abstract Animation<Sprite> cornerB();
	public abstract Animation<Sprite> cornerAB();
	public abstract Animation<Sprite> cornerC();
	public abstract Animation<Sprite> cornerAC();
	public abstract Animation<Sprite> cornerBC();
	public abstract Animation<Sprite> cornerABC();
	public abstract Animation<Sprite> cornerD();
	public abstract Animation<Sprite> cornerAD();
	public abstract Animation<Sprite> cornerBD();
	public abstract Animation<Sprite> cornerABD();
	public abstract Animation<Sprite> cornerCD();
	public abstract Animation<Sprite> cornerACD();
	public abstract Animation<Sprite> cornerBCD();
	public abstract Animation<Sprite> cornerABCD();
	public abstract Animation<Sprite> none();
	public abstract Animation<Sprite> cornerABRight();
	public abstract Animation<Sprite> cornerBCDown();
	public abstract Animation<Sprite> cornerCDLeft();
	public abstract Animation<Sprite> cornerADUp();
	public abstract Animation<Sprite> cornerARight();
	public abstract Animation<Sprite> cornerBRight();
	public abstract Animation<Sprite> cornerBDown();
	public abstract Animation<Sprite> cornerCDown();
	public abstract Animation<Sprite> cornerDLeft();
	public abstract Animation<Sprite> cornerCLeft();
	public abstract Animation<Sprite> cornerAUp();
	public abstract Animation<Sprite> cornerDUp();
	public abstract Animation<Sprite> cornerBDownRight();
	public abstract Animation<Sprite> cornerCDownLeft();
	public abstract Animation<Sprite> cornerDUpLeft();
	public abstract Animation<Sprite> cornerAUpRight();
}
