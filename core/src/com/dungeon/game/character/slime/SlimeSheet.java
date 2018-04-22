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

	private final Animation<TextureRegion> IDLE_ANIMATION = loop(0.25f, IDLE_1, IDLE_2, IDLE_3, IDLE_2);
	private final Animation<TextureRegion> ATTACK_ANIMATION = loop(0.1f, IDLE_3, ATTACK_1, ATTACK_2, ATTACK_1);

	public static final String IDLE = "slime_idle";
	public static final String ATTACK = "slime_attack";

	public SlimeSheet() {
		super(ResourceManager.instance().getTexture("slime.png"), 32);
	}

	public static Animation<TextureRegion> idle() {
		return new SlimeSheet().IDLE_ANIMATION;
	}

	public static Animation<TextureRegion> attack() {
		return new SlimeSheet().ATTACK_ANIMATION;
	}

}
