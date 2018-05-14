package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class SlimeSpawnSheet extends TileSheet {

	private final TextureRegion IDLE_1 = getTile(0, 0);
	private final TextureRegion IDLE_2 = getTile(1, 0);
	private final TextureRegion IDLE_3 = getTile(2, 0);

	private final TextureRegion BLINK_1 = getTile(3, 0);
	private final TextureRegion BLINK_2 = getTile(0, 1);

	private final TextureRegion DIE_1 = getTile(1, 1);
	private final TextureRegion DIE_2 = getTile(2, 1);
	private final TextureRegion DIE_3 = getTile(3, 1);
	private final TextureRegion DIE_4 = getTile(0, 2);
	private final TextureRegion DIE_5 = getTile(1, 2);
	private final TextureRegion DIE_6 = getTile(2, 2);
	private final TextureRegion DIE_7 = getTile(3, 2);
	private final TextureRegion DIE_8 = getTile(0, 3);

	private final Animation<TextureRegion> IDLE_ANIMATION = loop(0.25f, IDLE_1, IDLE_2, IDLE_3, IDLE_2);
	private final Animation<TextureRegion> BLINK_ANIMATION = loop(0.1f, IDLE_3, BLINK_1, BLINK_2, BLINK_2, BLINK_2, BLINK_1, IDLE_3, IDLE_3, IDLE_3, IDLE_3);
	private final Animation<TextureRegion> DIE_ANIMATION = new Animation<>(0.07f, DIE_1, DIE_2, DIE_3, DIE_4, DIE_5, DIE_6, DIE_7, DIE_8);

	public static final String IDLE = "slime_mini_idle";
	public static final String BLINK = "slime_mini_blink";
	public static final String DIE = "slime_mini_die";

	public SlimeSpawnSheet() {
		super(ResourceManager.instance().getTexture("slime_mini.png"), 16);
	}

	public static Animation<TextureRegion> idle() {
		return new SlimeSpawnSheet().IDLE_ANIMATION;
	}

	public static Animation<TextureRegion> blink() {
		return new SlimeSpawnSheet().BLINK_ANIMATION;
	}

	public static Animation<TextureRegion> die() {
		return new SlimeSpawnSheet().DIE_ANIMATION;
	}

}
