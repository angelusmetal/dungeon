package com.dungeon.game.object.exit;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class ExitPlatformSheet extends TileSheet {

	private final TextureRegion IDLE_1 = getTile(0, 0);
	private final Animation<TextureRegion> IDLE_ANIMATION = new Animation<>(1f, IDLE_1);

	public static final String IDLE = "exit_platform_idle";

	ExitPlatformSheet() {
		super(ResourceManager.instance().getTexture("exit_altar.png"), 64);
	}

	public static Animation<TextureRegion> idle() {
		return new ExitPlatformSheet().IDLE_ANIMATION;
	}

}
