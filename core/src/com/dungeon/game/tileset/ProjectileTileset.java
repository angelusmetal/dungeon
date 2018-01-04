package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;

public class ProjectileTileset extends Tileset {

	private final TextureRegion PROJECTILE_ASSASIN_FLY = getTile(0, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_1 = getTile(1, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_2 = getTile(2, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_3 = getTile(3, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_4 = getTile(4, 0);
	private final TextureRegion PROJECTILE_ASSASIN_EXPLODE_5 = getTile(5, 0);
	public final Animation<TextureRegion> PROJECTILE_ASSASIN_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_ASSASIN_FLY);
	public final Animation<TextureRegion> PROJECTILE_ASSASIN_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_ASSASIN_EXPLODE_1, PROJECTILE_ASSASIN_EXPLODE_2, PROJECTILE_ASSASIN_EXPLODE_3, PROJECTILE_ASSASIN_EXPLODE_4, PROJECTILE_ASSASIN_EXPLODE_5);

	private final TextureRegion PROJECTILE_THIEF_FLY = getTile(0, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_1 = getTile(1, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_2 = getTile(2, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_3 = getTile(3, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_4 = getTile(4, 1);
	private final TextureRegion PROJECTILE_THIEF_EXPLODE_5 = getTile(5, 1);
	public final Animation<TextureRegion> PROJECTILE_THIEF_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_THIEF_FLY);
	public final Animation<TextureRegion> PROJECTILE_THIEF_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_THIEF_EXPLODE_1, PROJECTILE_THIEF_EXPLODE_2, PROJECTILE_THIEF_EXPLODE_3, PROJECTILE_THIEF_EXPLODE_4, PROJECTILE_THIEF_EXPLODE_5);

	private final TextureRegion PROJECTILE_WITCH_FLY = getTile(0, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_1 = getTile(1, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_2 = getTile(2, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_3 = getTile(3, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_4 = getTile(4, 2);
	private final TextureRegion PROJECTILE_WITCH_EXPLODE_5 = getTile(5, 2);
	public final Animation<TextureRegion> PROJECTILE_WITCH_FLY_ANIMATION = new Animation<>(0.1f, PROJECTILE_WITCH_FLY);
	public final Animation<TextureRegion> PROJECTILE_WITCH_EXPLODE_ANIMATION = new Animation<>(0.1f, PROJECTILE_WITCH_EXPLODE_1, PROJECTILE_WITCH_EXPLODE_2, PROJECTILE_WITCH_EXPLODE_3, PROJECTILE_WITCH_EXPLODE_4, PROJECTILE_WITCH_EXPLODE_5);

	ProjectileTileset() {
		super(new Texture("projectile.png"), 16);
	}

}
