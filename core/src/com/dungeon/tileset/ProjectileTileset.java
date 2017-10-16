package com.dungeon.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.tileset.Tileset;

public class ProjectileTileset extends Tileset {

	private final TextureRegion PROJECTILE_FLY = getTile(0, 0);
	private final TextureRegion PROJECTILE_EXPLODE_1 = getTile(1, 0);
	private final TextureRegion PROJECTILE_EXPLODE_2 = getTile(2, 0);
	private final TextureRegion PROJECTILE_EXPLODE_3 = getTile(3, 0);
	private final TextureRegion PROJECTILE_EXPLODE_4 = getTile(4, 0);
	private final TextureRegion PROJECTILE_EXPLODE_5 = getTile(5, 0);
	public final Animation<TextureRegion> PROJECTILE_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_FLY);
	public final Animation<TextureRegion> PROJECTILE_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_EXPLODE_1, PROJECTILE_EXPLODE_2, PROJECTILE_EXPLODE_3, PROJECTILE_EXPLODE_4, PROJECTILE_EXPLODE_5);

	ProjectileTileset() {
		super(new Texture("projectile.png"), 16);
	}

}
