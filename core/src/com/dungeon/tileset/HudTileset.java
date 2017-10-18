package com.dungeon.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HudTileset extends Tileset {

	public final TextureRegion HEALTH_BAR = getTile(0, 0, 2, 2);

	public HudTileset() {
		super(new Texture("hud.png"), 1);
	}
}
