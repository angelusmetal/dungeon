package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class CatProjectileSheet extends TileSheet {

	private final TextureRegion PROJECTILE_FLY_RIGHT_1 = getTile(0, 0);
	private final TextureRegion PROJECTILE_FLY_RIGHT_2 = getTile(1, 0);
	private final TextureRegion PROJECTILE_FLY_RIGHT_3 = getTile(2, 0);
	private final TextureRegion PROJECTILE_FLY_RIGHT_4 = getTile(3, 0);
	private final TextureRegion PROJECTILE_FLY_UP_1 = getTile(0, 1);
	private final TextureRegion PROJECTILE_FLY_UP_2 = getTile(1, 1);
	private final TextureRegion PROJECTILE_FLY_UP_3 = getTile(2, 1);
	private final TextureRegion PROJECTILE_FLY_UP_4 = getTile(3, 1);
	private final TextureRegion PROJECTILE_FLY_DOWN_1 = getTile(0, 2);
	private final TextureRegion PROJECTILE_FLY_DOWN_2 = getTile(1, 2);
	private final TextureRegion PROJECTILE_FLY_DOWN_3 = getTile(2, 2);
	private final TextureRegion PROJECTILE_FLY_DOWN_4 = getTile(3, 2);
	private final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION_RIGHT = loop(0.25f, PROJECTILE_FLY_RIGHT_1, PROJECTILE_FLY_RIGHT_2, PROJECTILE_FLY_RIGHT_3, PROJECTILE_FLY_RIGHT_4);
	private final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION_UP = loop(0.25f, PROJECTILE_FLY_UP_1, PROJECTILE_FLY_UP_2, PROJECTILE_FLY_UP_3, PROJECTILE_FLY_UP_4);
	private final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION_DOWN = loop(0.25f, PROJECTILE_FLY_DOWN_1, PROJECTILE_FLY_DOWN_2, PROJECTILE_FLY_DOWN_3, PROJECTILE_FLY_DOWN_4);

	public static final String FLY_RIGHT = "projectile_cat_right";
	public static final String FLY_UP = "projectile_cat_up";
	public static final String FLY_DOWN = "projectile_cat_down";

	CatProjectileSheet() {
		super(ResourceManager.getTexture("cat_projectile.png"), 24);
	}

	public static Animation<TextureRegion> flyRight() {
		return new CatProjectileSheet().PROJECTILE_FLY_ANIMATION_RIGHT;
	}

	public static Animation<TextureRegion> flyUp() {
		return new CatProjectileSheet().PROJECTILE_FLY_ANIMATION_UP;
	}

	public static Animation<TextureRegion> flyDown() {
		return new CatProjectileSheet().PROJECTILE_FLY_ANIMATION_DOWN;
	}

}
