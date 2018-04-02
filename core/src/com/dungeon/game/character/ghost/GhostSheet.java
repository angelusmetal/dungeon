package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;
import com.dungeon.engine.resource.ResourceManager;

public class GhostSheet extends Tileset {

	private final TextureRegion HOVER_1 = getTile(0, 0);
	private final TextureRegion HOVER_2 = getTile(1, 0);
	private final TextureRegion HOVER_3 = getTile(2, 0);
	private final TextureRegion HOVER_4 = getTile(3, 0);
	private final TextureRegion HOVER_5 = getTile(0, 1);
	private final TextureRegion HOVER_6 = getTile(1, 1);
	private final TextureRegion HOVER_7 = getTile(2, 1);
	private final TextureRegion HOVER_8 = getTile(3, 1);
	private final TextureRegion HOVER_9 = getTile(0, 2);
	private final Animation<TextureRegion> HOVER_ANIMATION = loop(0.1f, HOVER_1, HOVER_2, HOVER_3, HOVER_4, HOVER_5, HOVER_6, HOVER_7, HOVER_8, HOVER_9);

	public static final String HOVER = "ghost_hover";

	public GhostSheet() {
		super(ResourceManager.instance().getTexture("ghost_animation.png"), 32);
	}

	public static Animation<TextureRegion> hover() {
		return new GhostSheet().HOVER_ANIMATION;
	}

}
