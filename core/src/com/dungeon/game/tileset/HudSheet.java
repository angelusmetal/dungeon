package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class HudSheet extends TileSheet {

	public final TextureRegion HEALTH_BAR = getTile(0, 0, 2, 2);

	public HudSheet() {
		super(ResourceManager.instance().getTexture("hud.png"), 1);
	}
}
