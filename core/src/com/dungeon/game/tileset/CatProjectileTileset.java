package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;

public class CatProjectileTileset extends Tileset {

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
	public final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION_RIGHT = new Animation<>(0.25f, PROJECTILE_FLY_RIGHT_1, PROJECTILE_FLY_RIGHT_2, PROJECTILE_FLY_RIGHT_3, PROJECTILE_FLY_RIGHT_4);
	public final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION_UP = new Animation<>(0.25f, PROJECTILE_FLY_UP_1, PROJECTILE_FLY_UP_2, PROJECTILE_FLY_UP_3, PROJECTILE_FLY_UP_4);
	public final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION_DOWN = new Animation<>(0.25f, PROJECTILE_FLY_DOWN_1, PROJECTILE_FLY_DOWN_2, PROJECTILE_FLY_DOWN_3, PROJECTILE_FLY_DOWN_4);

	CatProjectileTileset() {
		super(new Texture("cat_projectile.png"), 24);
	}

}
