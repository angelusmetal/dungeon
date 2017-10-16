package com.dungeon.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.tileset.Tileset;

public class KnightTileset extends Tileset {

	public final TextureRegion WALK_SOUTH_1 = getTile(0, 0);
	public final TextureRegion WALK_SOUTH_2 = getTile(1, 0);
	public final TextureRegion WALK_SOUTH_3 = getTile(2, 0);
	public final TextureRegion WALK_SOUTH_4 = getTile(3, 0);
	public final TextureRegion WALK_SOUTH_5 = getTile(4, 0);
	public final TextureRegion WALK_SOUTH_6 = getTile(5, 0);
	public final TextureRegion WALK_SOUTH_7 = getTile(6, 0);
	public final TextureRegion WALK_SOUTH_8 = getTile(7, 0);
	public final Animation<TextureRegion> WALK_SOUTH_ANIMATION = new Animation<>(0.1f, WALK_SOUTH_1, WALK_SOUTH_2, WALK_SOUTH_3, WALK_SOUTH_4, WALK_SOUTH_5, WALK_SOUTH_6, WALK_SOUTH_7, WALK_SOUTH_8);

	public KnightTileset() {
		super(new Texture("knight_animation_64.png"), 64);
	}

}
