package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class SlimeSheet extends TileSheet {

	private final TextureRegion IDLE_1 = getTile(0, 0);
	private final TextureRegion IDLE_2 = getTile(1, 0);
	private final TextureRegion IDLE_3 = getTile(2, 0);

	private final TextureRegion ATTACK_1 = getTile(3, 0);
	private final TextureRegion ATTACK_2 = getTile(0, 1);

	private final TextureRegion DIE_1 = getTile(1, 1);
	private final TextureRegion DIE_2 = getTile(2, 1);
	private final TextureRegion DIE_3 = getTile(3, 1);
	private final TextureRegion DIE_4 = getTile(0, 2);
	private final TextureRegion DIE_5 = getTile(1, 2);
	private final TextureRegion DIE_6 = getTile(2, 2);
	private final TextureRegion DIE_7 = getTile(3, 2);
	private final TextureRegion DIE_8 = getTile(0, 3);

	private final Animation<TextureRegion> IDLE_ANIMATION = loop(0.25f, IDLE_1, IDLE_2, IDLE_3, IDLE_2);
	private final Animation<TextureRegion> ATTACK_ANIMATION = loop(0.1f, IDLE_3, ATTACK_1, ATTACK_2, ATTACK_1);
	private final Animation<TextureRegion> DIE_ANIMATION = new Animation<>(0.07f, DIE_1, DIE_2, DIE_3, DIE_4, DIE_5, DIE_6, DIE_7, DIE_8);

	public static final String IDLE = "slime_idle";
	public static final String ATTACK = "slime_attack";
	public static final String DIE = "slime_die";

	public SlimeSheet() {
		super(ResourceManager.instance().getTexture("slime.png"), 32);
	}

	public static Animation<TextureRegion> idle() {
		return new SlimeSheet().IDLE_ANIMATION;
	}

	public static Animation<TextureRegion> attack() {
		return new SlimeSheet().ATTACK_ANIMATION;
	}

	public static Animation<TextureRegion> die() {
		return new SlimeSheet().DIE_ANIMATION;
	}

}
