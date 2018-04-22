package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class ProjectileSheet extends TileSheet {

	private final TextureRegion PROJECTILE_ASSASIN_FLY = getTile(0, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_1 = getTile(1, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_2 = getTile(2, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_3 = getTile(3, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_4 = getTile(4, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_5 = getTile(5, 0);
	private final Animation<TextureRegion> PROJECTILE_ASSASIN_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_ASSASIN_FLY);
	private final Animation<TextureRegion> PROJECTILE_ASSASIN_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_ASSASIN_EXPLODE_1, PROJECTILE_ASSASIN_EXPLODE_2, PROJECTILE_ASSASIN_EXPLODE_3, PROJECTILE_ASSASIN_EXPLODE_4, PROJECTILE_ASSASIN_EXPLODE_5);

	private final TextureRegion PROJECTILE_THIEF_FLY = getTile(0, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_1 = getTile(1, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_2 = getTile(2, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_3 = getTile(3, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_4 = getTile(4, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_5 = getTile(5, 1);
	private final Animation<TextureRegion> PROJECTILE_THIEF_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_THIEF_FLY);
	private final Animation<TextureRegion> PROJECTILE_THIEF_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_THIEF_EXPLODE_1, PROJECTILE_THIEF_EXPLODE_2, PROJECTILE_THIEF_EXPLODE_3, PROJECTILE_THIEF_EXPLODE_4, PROJECTILE_THIEF_EXPLODE_5);

	private final TextureRegion PROJECTILE_WITCH_FLY = getTile(0, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_1 = getTile(1, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_2 = getTile(2, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_3 = getTile(3, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_4 = getTile(4, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_5 = getTile(5, 2);
	private final Animation<TextureRegion> PROJECTILE_WITCH_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_WITCH_FLY);
	private final Animation<TextureRegion> PROJECTILE_WITCH_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_WITCH_EXPLODE_1, PROJECTILE_WITCH_EXPLODE_2, PROJECTILE_WITCH_EXPLODE_3, PROJECTILE_WITCH_EXPLODE_4, PROJECTILE_WITCH_EXPLODE_5);

	public static final String ASSASSIN_FLY = "projectile_assassin_fly";
	public static final String ASSASSIN_EXPLODE = "projectile_assassin_explode";
	public static final String THIEF_FLY = "projectile_thief_fly";
	public static final String THIEF_EXPLODE = "projectile_thief_explode";
	public static final String WITCH_FLY = "projectile_witch_fly";
	public static final String WITCH_EXPLODE = "projectile_witch_explode";

	ProjectileSheet() {
		super(ResourceManager.instance().getTexture("projectile.png"), 16);
	}

	public static Animation<TextureRegion> assasinFly() {
		return new ProjectileSheet().PROJECTILE_ASSASIN_FLY_ANIMATION;
	}

	public static Animation<TextureRegion> assasinExplode() {
		return new ProjectileSheet().PROJECTILE_ASSASIN_EXPLODE_ANIMATION;
	}

	public static Animation<TextureRegion> thiefFly() {
		return new ProjectileSheet().PROJECTILE_THIEF_FLY_ANIMATION;
	}

	public static Animation<TextureRegion> thiefExplode() {
		return new ProjectileSheet().PROJECTILE_THIEF_EXPLODE_ANIMATION;
	}

	public static Animation<TextureRegion> witchFly() {
		return new ProjectileSheet().PROJECTILE_WITCH_FLY_ANIMATION;
	}

	public static Animation<TextureRegion> witchExplode() {
		return new ProjectileSheet().PROJECTILE_WITCH_EXPLODE_ANIMATION;
	}

}
