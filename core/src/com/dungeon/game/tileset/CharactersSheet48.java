package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.game.resource.Resources;

public class CharactersSheet48 extends TileSheet {

	private final TextureRegion WITCH_IDLE_1 = getTile(0, 0);
	private final TextureRegion WITCH_IDLE_2 = getTile(1, 0);
	private final TextureRegion WITCH_WALK_1 = getTile(2, 0);
	private final TextureRegion WITCH_WALK_2 = getTile(3, 0);
	private final TextureRegion WITCH_WALK_3 = getTile(4, 0);
	private final TextureRegion WITCH_WALK_4 = getTile(5, 0);
	private final TextureRegion WITCH_WALK_5 = getTile(6, 0);
	private final TextureRegion WITCH_WALK_6 = getTile(7, 0);
	private final TextureRegion WITCH_WALK_7 = getTile(8, 0);
	private final TextureRegion WITCH_WALK_8 = getTile(9, 0);
	private final TextureRegion WITCH_ATTACK_1 = getTile(10, 0);
	private final TextureRegion WITCH_ATTACK_2 = getTile(11, 0);
	private final TextureRegion WITCH_ATTACK_3 = getTile(12, 0);
	private final TextureRegion WITCH_ATTACK_4 = getTile(13, 0);
	private final TextureRegion WITCH_ATTACK_5 = getTile(14, 0);
	private final TextureRegion WITCH_ATTACK_6 = getTile(15, 0);
	private final TextureRegion WITCH_ATTACK_7 = getTile(16, 0);
	private final TextureRegion WITCH_ATTACK_8 = getTile(17, 0);

	private final Animation<TextureRegion> WITCH_WALK_ANIMATION = loop(0.1f, WITCH_WALK_1, WITCH_WALK_2, WITCH_WALK_3, WITCH_WALK_4, WITCH_WALK_5, WITCH_WALK_6, WITCH_WALK_7, WITCH_WALK_8);
	private final Animation<TextureRegion> WITCH_ATTACK_ANIMATION = new Animation<>(0.05f, WITCH_ATTACK_1, WITCH_ATTACK_2, WITCH_ATTACK_3, WITCH_ATTACK_4, WITCH_ATTACK_5, WITCH_ATTACK_6, WITCH_ATTACK_7, WITCH_ATTACK_8);
	private final Animation<TextureRegion> WITCH_IDLE_ANIMATION = loop(0.7f, WITCH_IDLE_1, WITCH_IDLE_2);

	public static final String WITCH_WALK = "witch_walk_2";
	public static final String WITCH_ATTACK = "witch_attack_2";
	public static final String WITCH_IDLE = "witch_idle_2";

	public CharactersSheet48() {
		super(Resources.textures.get("character_sheet_48.png"), 48);
	}

	public static Animation<TextureRegion> witchWalk() {
		return new CharactersSheet48().WITCH_WALK_ANIMATION;
	}

	public static Animation<TextureRegion> witchAttack() {
		return new CharactersSheet48().WITCH_ATTACK_ANIMATION;
	}

	public static Animation<TextureRegion> witchIdle() {
		return new CharactersSheet48().WITCH_IDLE_ANIMATION;
	}

}
