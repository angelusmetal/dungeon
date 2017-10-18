package com.dungeon.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.tileset.Tileset;

public class GhostTileset extends Tileset {

	public final TextureRegion HOVER_1 = getTile(0, 0);
	public final TextureRegion HOVER_2 = getTile(1, 0);
	public final TextureRegion HOVER_3 = getTile(2, 0);
	public final TextureRegion HOVER_4 = getTile(3, 0);
	public final TextureRegion HOVER_5 = getTile(0, 1);
	public final TextureRegion HOVER_6 = getTile(1, 1);
	public final TextureRegion HOVER_7 = getTile(2, 1);
	public final TextureRegion HOVER_8 = getTile(3, 1);
	public final TextureRegion HOVER_9 = getTile(0, 2);
	public final Animation<TextureRegion> HOVER_ANIMATION = loop(0.1f, HOVER_1, HOVER_2, HOVER_3, HOVER_4, HOVER_5, HOVER_6, HOVER_7, HOVER_8, HOVER_9);

	public GhostTileset() {
		super(new Texture("ghost_animation.png"), 32);
	}

}
