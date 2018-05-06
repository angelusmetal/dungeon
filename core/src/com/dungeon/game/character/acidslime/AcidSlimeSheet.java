package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class AcidSlimeSheet extends TileSheet {

	private final TextureRegion IDLE_1 = getTile(0, 0);
	private final TextureRegion IDLE_2 = getTile(1, 0);
	private final TextureRegion IDLE_3 = getTile(2, 0);
	private final TextureRegion IDLE_4 = getTile(3, 0);
	private final TextureRegion IDLE_5 = getTile(4, 0);
	private final TextureRegion IDLE_6 = getTile(5, 0);
	private final TextureRegion IDLE_7 = getTile(6, 0);
	private final TextureRegion IDLE_8 = getTile(7, 0);
	private final TextureRegion IDLE_9 = getTile(0, 1);

	private final TextureRegion ATTACK_1 = getTile(1, 1);
	private final TextureRegion ATTACK_2 = getTile(2, 1);
	private final TextureRegion ATTACK_3 = getTile(3, 1);
	private final TextureRegion ATTACK_4 = getTile(4, 1);
	private final TextureRegion ATTACK_5 = getTile(5, 1);
	private final TextureRegion ATTACK_6 = getTile(6, 1);
	private final TextureRegion ATTACK_7 = getTile(7, 1);
	private final TextureRegion ATTACK_8 = getTile(0, 2);
	private final TextureRegion ATTACK_9 = getTile(1, 2);

	private final TextureRegion DIE_1 = getTile(2, 2);
	private final TextureRegion DIE_2 = getTile(3, 2);
	private final TextureRegion DIE_3 = getTile(4, 2);
	private final TextureRegion DIE_4 = getTile(5, 2);
	private final TextureRegion DIE_5 = getTile(6, 2);
	private final TextureRegion DIE_6 = getTile(7, 2);
	private final TextureRegion DIE_7 = getTile(0, 3);
	private final TextureRegion DIE_8 = getTile(1, 3);
	private final TextureRegion DIE_9 = getTile(2, 3);

	private final Animation<TextureRegion> IDLE_ANIMATION = loop(0.1f, IDLE_1, IDLE_2, IDLE_3, IDLE_4, IDLE_5, IDLE_6, IDLE_7, IDLE_8, IDLE_9);
	private final Animation<TextureRegion> ATTACK_ANIMATION = loop(0.1f, ATTACK_1, ATTACK_2, ATTACK_3, ATTACK_4, ATTACK_5, ATTACK_6, ATTACK_7, ATTACK_8, ATTACK_9);
	private final Animation<TextureRegion> DIE_ANIMATION = new Animation<>(0.07f, DIE_1, DIE_2, DIE_3, DIE_4, DIE_5, DIE_6, DIE_7, DIE_8, DIE_9);

	public static final String IDLE = "slime_acid_idle";
	public static final String ATTACK = "slime_acid_attack";
	public static final String DIE = "slime_acid_die";

	public AcidSlimeSheet() {
		super(ResourceManager.instance().getTexture("acid_slime.png"), 32);
	}

	public static Animation<TextureRegion> idle() {
		return new AcidSlimeSheet().IDLE_ANIMATION;
	}

	public static Animation<TextureRegion> attack() {
		return new AcidSlimeSheet().ATTACK_ANIMATION;
	}

	public static Animation<TextureRegion> die() {
		return new AcidSlimeSheet().DIE_ANIMATION;
	}

}
