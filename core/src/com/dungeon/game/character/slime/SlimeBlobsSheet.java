package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class SlimeBlobsSheet extends TileSheet {

	private final TextureRegion POOL_1 = getTile(0, 0, 2, 1);
	private final TextureRegion POOL_2 = getTile(2, 0, 2, 1);
	private final TextureRegion POOL_3 = getTile(4, 0, 2, 1);
	private final TextureRegion POOL_4 = getTile(6, 0, 2, 1);
	private final TextureRegion POOL_5 = getTile(8, 0, 2, 1);

	private final TextureRegion BLOB_1 = getTile(0, 1, 1, 1);
	private final TextureRegion BLOB_2 = getTile(1, 1, 1, 1);
	private final TextureRegion BLOB_3 = getTile(2, 1, 1, 1);
	private final TextureRegion BLOB_4 = getTile(3, 1, 1, 1);

	private final TextureRegion SPLAT_1 = getTile(4, 1, 1, 1);
	private final TextureRegion SPLAT_2 = getTile(5, 1, 1, 1);
	private final TextureRegion SPLAT_3 = getTile(6, 1, 1, 1);
	private final TextureRegion SPLAT_4 = getTile(7, 1, 1, 1);
	private final TextureRegion SPLAT_5 = getTile(8, 1, 1, 1);
	private final TextureRegion SPLAT_6 = getTile(9, 1, 1, 1);

	private final Animation<TextureRegion> POOL_FLOOD_ANIMATION = new Animation<>(0.1f, POOL_1, POOL_2, POOL_3, POOL_4, POOL_5);
	private final Animation<TextureRegion> POOL_DRY_ANIMATION = new Animation<>(0.1f, POOL_5, POOL_4, POOL_3, POOL_2, POOL_1);
	private final Animation<TextureRegion> BLOB_ANIMATION = loop(0.1f, BLOB_1, BLOB_2, BLOB_3, BLOB_4);
	private final Animation<TextureRegion> SPLAT_ANIMATION = new Animation<>(0.1f, SPLAT_1, SPLAT_2, SPLAT_3, SPLAT_4, SPLAT_5, SPLAT_6);

	public static final String POOL_FLOOD = "slime_pool_flood";
	public static final String POOL_DRY = "slime_pool_dry";
	public static final String BLOB = "slime_blob";
	public static final String SPLAT = "slime_splat";

	public SlimeBlobsSheet() {
		super(ResourceManager.getTexture("slime_blobs.png"), 16);
	}

	public static Animation<TextureRegion> poolFlood() {
		return new SlimeBlobsSheet().POOL_FLOOD_ANIMATION;
	}

	public static Animation<TextureRegion> poolDry() {
		return new SlimeBlobsSheet().POOL_DRY_ANIMATION;
	}

	public static Animation<TextureRegion> blob() {
		return new SlimeBlobsSheet().BLOB_ANIMATION;
	}

	public static Animation<TextureRegion> splat() {
		return new SlimeBlobsSheet().SPLAT_ANIMATION;
	}

}
