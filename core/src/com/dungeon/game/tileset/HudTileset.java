package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;
import com.dungeon.engine.resource.ResourceManager;

public class HudTileset extends Tileset {

	public final TextureRegion HEALTH_BAR = getTile(0, 0, 2, 2);

	public HudTileset() {
		super(ResourceManager.instance().getTexture("hud.png"), 1);
	}
}
