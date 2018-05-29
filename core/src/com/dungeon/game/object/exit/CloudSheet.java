package com.dungeon.game.object.exit;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class CloudSheet extends TileSheet {

	private final TextureRegion IDLE_1 = getTile(0, 0);
	private final Animation<TextureRegion> IDLE_ANIMATION = new Animation<>(1f, IDLE_1);

	public static final String IDLE = "cloud";

	CloudSheet() {
		super(ResourceManager.getTexture("cloud.png"), 64);
	}

	public static Animation<TextureRegion> idle() {
		return new CloudSheet().IDLE_ANIMATION;
	}

}
