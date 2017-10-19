package com.dungeon.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TombstoneTileset extends Tileset {

	private final TextureRegion TOMBSTONE_1 = getTile(0, 0);
	private final TextureRegion TOMBSTONE_2 = getTile(1, 0);
	private final TextureRegion TOMBSTONE_3 = getTile(2, 0);
	private final TextureRegion TOMBSTONE_4 = getTile(3, 0);
	private final TextureRegion TOMBSTONE_5 = getTile(4, 0);
	private final TextureRegion TOMBSTONE_6 = getTile(5, 0);
	private final TextureRegion TOMBSTONE_7 = getTile(0, 1);
	private final TextureRegion TOMBSTONE_8 = getTile(1, 1);
	private final TextureRegion TOMBSTONE_9 = getTile(2, 1);
	private final TextureRegion TOMBSTONE_10 = getTile(3, 1);
	private final TextureRegion TOMBSTONE_11 = getTile(4, 1);
	private final TextureRegion TOMBSTONE_12 = getTile(5, 1);
	public final Animation<TextureRegion> TOMBSTONE_SPAWN_ANIMATION = new Animation<>(0.1f, TOMBSTONE_1, TOMBSTONE_2, TOMBSTONE_3, TOMBSTONE_4, TOMBSTONE_5, TOMBSTONE_6, TOMBSTONE_7, TOMBSTONE_8, TOMBSTONE_9, TOMBSTONE_10, TOMBSTONE_11, TOMBSTONE_12);

	TombstoneTileset() {
		super(new Texture("tombstone.png"), 32);
	}

}
