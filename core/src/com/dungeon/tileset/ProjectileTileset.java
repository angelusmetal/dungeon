package com.dungeon.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ProjectileTileset extends Tileset {

	private final TextureRegion PROJECTILE_KING_FLY = getTile(0, 0);
	private final TextureRegion PROJECTILE_KING_EXPLODE_1 = getTile(1, 0);
	private final TextureRegion PROJECTILE_KING_EXPLODE_2 = getTile(2, 0);
	private final TextureRegion PROJECTILE_KING_EXPLODE_3 = getTile(3, 0);
	private final TextureRegion PROJECTILE_KING_EXPLODE_4 = getTile(4, 0);
	private final TextureRegion PROJECTILE_KING_EXPLODE_5 = getTile(5, 0);
	public final Animation<TextureRegion> PROJECTILE_KING_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_KING_FLY);
	public final Animation<TextureRegion> PROJECTILE_KING_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_KING_EXPLODE_1, PROJECTILE_KING_EXPLODE_2, PROJECTILE_KING_EXPLODE_3, PROJECTILE_KING_EXPLODE_4, PROJECTILE_KING_EXPLODE_5);

	private final TextureRegion PROJECTILE_THIEF_FLY = getTile(0, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_1 = getTile(1, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_2 = getTile(2, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_3 = getTile(3, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_4 = getTile(4, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_5 = getTile(5, 1);
	public final Animation<TextureRegion> PROJECTILE_THIEF_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_THIEF_FLY);
	public final Animation<TextureRegion> PROJECTILE_THIEF_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_THIEF_EXPLODE_1, PROJECTILE_THIEF_EXPLODE_2, PROJECTILE_THIEF_EXPLODE_3, PROJECTILE_THIEF_EXPLODE_4, PROJECTILE_THIEF_EXPLODE_5);

	private final TextureRegion PROJECTILE_MONK_FLY = getTile(0, 2);
	private final TextureRegion PROJECTILE_MONK_EXPLODE_1 = getTile(1, 2);
	private final TextureRegion PROJECTILE_MONK_EXPLODE_2 = getTile(2, 2);
	private final TextureRegion PROJECTILE_MONK_EXPLODE_3 = getTile(3, 2);
	private final TextureRegion PROJECTILE_MONK_EXPLODE_4 = getTile(4, 2);
	private final TextureRegion PROJECTILE_MONK_EXPLODE_5 = getTile(5, 2);
	public final Animation<TextureRegion> PROJECTILE_MONK_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_MONK_FLY);
	public final Animation<TextureRegion> PROJECTILE_MONK_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_MONK_EXPLODE_1, PROJECTILE_MONK_EXPLODE_2, PROJECTILE_MONK_EXPLODE_3, PROJECTILE_MONK_EXPLODE_4, PROJECTILE_MONK_EXPLODE_5);

	ProjectileTileset() {
		super(new Texture("projectile.png"), 16);
	}

}
