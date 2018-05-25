package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;

public class CharactersSheet32 extends TileSheet {

	private final TextureRegion WITCH_WALK_1 = getTile(0, 0);
	private final TextureRegion WITCH_WALK_2 = getTile(1, 0);
	private final TextureRegion WITCH_WALK_3 = getTile(2, 0);
	private final TextureRegion WITCH_WALK_4 = getTile(3, 0);
	private final TextureRegion WITCH_WALK_5 = getTile(4, 0);
	private final TextureRegion WITCH_WALK_6 = getTile(5, 0);
	private final TextureRegion WITCH_WALK_7 = getTile(6, 0);
	private final TextureRegion WITCH_WALK_8 = getTile(7, 0);
	private final TextureRegion WITCH_WALK_9 = getTile(8, 0);
	private final TextureRegion WITCH_ATTACK_1 = getTile(9, 0);
	private final TextureRegion WITCH_ATTACK_2 = getTile(10, 0);
	private final TextureRegion WITCH_ATTACK_3 = getTile(11, 0);
	private final TextureRegion WITCH_ATTACK_4 = getTile(12, 0);
	private final TextureRegion WITCH_ATTACK_5 = getTile(13, 0);
	private final TextureRegion WITCH_ATTACK_6 = getTile(14, 0);
	private final TextureRegion WITCH_ATTACK_7 = getTile(15, 0);
	private final TextureRegion WITCH_ATTACK_8 = getTile(16, 0);
	private final TextureRegion WITCH_ATTACK_9 = getTile(17, 0);
	private final TextureRegion WITCH_ATTACK_10 = getTile(18, 0);
	private final TextureRegion WITCH_IDLE_1 = getTile(20, 0);
	private final TextureRegion WITCH_IDLE_2 = getTile(21, 0);
	private final TextureRegion WITCH_IDLE_3 = getTile(22, 0);

	private final TextureRegion ASSASSIN_WALK_1 = getTile(0, 1);
	private final TextureRegion ASSASSIN_WALK_2 = getTile(1, 1);
	private final TextureRegion ASSASSIN_WALK_3 = getTile(2, 1);
	private final TextureRegion ASSASSIN_WALK_4 = getTile(3, 1);
	private final TextureRegion ASSASSIN_WALK_5 = getTile(4, 1);
	private final TextureRegion ASSASSIN_WALK_6 = getTile(5, 1);
	private final TextureRegion ASSASSIN_WALK_7 = getTile(6, 1);
	private final TextureRegion ASSASSIN_WALK_8 = getTile(7, 1);
	private final TextureRegion ASSASSIN_WALK_9 = getTile(8, 1);
	private final TextureRegion ASSASSIN_ATTACK_1 = getTile(9, 1);
	private final TextureRegion ASSASSIN_ATTACK_2 = getTile(10, 1);
	private final TextureRegion ASSASSIN_ATTACK_3 = getTile(11, 1);
	private final TextureRegion ASSASSIN_ATTACK_4 = getTile(12, 1);
	private final TextureRegion ASSASSIN_ATTACK_5 = getTile(13, 1);
	private final TextureRegion ASSASSIN_ATTACK_6 = getTile(14, 1);
	private final TextureRegion ASSASSIN_ATTACK_7 = getTile(15, 1);
	private final TextureRegion ASSASSIN_ATTACK_8 = getTile(16, 1);
	private final TextureRegion ASSASSIN_ATTACK_9 = getTile(17, 1);
	private final TextureRegion ASSASSIN_ATTACK_10 = getTile(18, 1);
	private final TextureRegion ASSASSIN_IDLE_1 = getTile(20, 1);
	private final TextureRegion ASSASSIN_IDLE_2 = getTile(21, 1);
	private final TextureRegion ASSASSIN_IDLE_3 = getTile(22, 1);

	private final TextureRegion THIEF_WALK_1 = getTile(0, 2);
	private final TextureRegion THIEF_WALK_2 = getTile(1, 2);
	private final TextureRegion THIEF_WALK_3 = getTile(2, 2);
	private final TextureRegion THIEF_WALK_4 = getTile(3, 2);
	private final TextureRegion THIEF_ATTACK_1 = getTile(8, 2);
	private final TextureRegion THIEF_ATTACK_2 = getTile(9, 2);
	private final TextureRegion THIEF_IDLE_1 = getTile(0, 2);
	private final TextureRegion THIEF_IDLE_2 = getTile(6, 2);

	private final Animation<TextureRegion> WITCH_WALK_ANIMATION = loop(0.05f, WITCH_WALK_1, WITCH_WALK_2, WITCH_WALK_3, WITCH_WALK_4, WITCH_WALK_5, WITCH_WALK_6, WITCH_WALK_7, WITCH_WALK_8, WITCH_WALK_9);
	private final Animation<TextureRegion> WITCH_ATTACK_ANIMATION = new Animation<>(0.08f, WITCH_ATTACK_1, WITCH_ATTACK_2, WITCH_ATTACK_3, WITCH_ATTACK_4, WITCH_ATTACK_5, WITCH_ATTACK_6, WITCH_ATTACK_7, WITCH_ATTACK_8, WITCH_ATTACK_9, WITCH_ATTACK_10);
	private final Animation<TextureRegion> WITCH_IDLE_ANIMATION = loop(0.3f, WITCH_IDLE_1, WITCH_IDLE_2, WITCH_IDLE_3, WITCH_IDLE_2);

	private final Animation<TextureRegion> ASSASSIN_WALK_ANIMATION = loop(0.05f, ASSASSIN_WALK_1, ASSASSIN_WALK_2, ASSASSIN_WALK_3, ASSASSIN_WALK_4, ASSASSIN_WALK_5, ASSASSIN_WALK_6/*, ASSASSIN_WALK_7, ASSASSIN_WALK_8, ASSASSIN_WALK_9*/);
	private final Animation<TextureRegion> ASSASSIN_ATTACK_ANIMATION = new Animation<>(0.02f, ASSASSIN_ATTACK_1, ASSASSIN_ATTACK_2, ASSASSIN_ATTACK_3, ASSASSIN_ATTACK_4, ASSASSIN_ATTACK_5, ASSASSIN_ATTACK_6, ASSASSIN_ATTACK_7, ASSASSIN_ATTACK_8, ASSASSIN_ATTACK_9, ASSASSIN_ATTACK_10);
	private final Animation<TextureRegion> ASSASSIN_IDLE_ANIMATION = loop(0.3f, ASSASSIN_IDLE_1, ASSASSIN_IDLE_2, ASSASSIN_IDLE_3, ASSASSIN_IDLE_2);

	private final Animation<TextureRegion> THIEF_WALK_ANIMATION = loop(0.1f, THIEF_WALK_1, THIEF_WALK_2, THIEF_WALK_3, THIEF_WALK_4);
	private final Animation<TextureRegion> THIEF_ATTACK_ANIMATION = new Animation<>(0.1f, THIEF_ATTACK_1, THIEF_ATTACK_2, THIEF_ATTACK_1);
	private final Animation<TextureRegion> THIEF_IDLE_ANIMATION = loop(0.5f, THIEF_IDLE_1, THIEF_IDLE_2);

	public static final String ASSASSIN_WALK = "assassin_walk";
	public static final String ASSASSIN_ATTACK = "assassin_attack";
	public static final String ASSASSIN_IDLE = "assassin_idle";

	public static final String THIEF_WALK = "thief_walk";
	public static final String THIEF_ATTACK = "thief_attack";
	public static final String THIEF_IDLE = "thief_idle";

	public static final String WITCH_WALK = "witch_walk";
	public static final String WITCH_ATTACK = "witch_attack";
	public static final String WITCH_IDLE = "witch_idle";

	public CharactersSheet32() {
		super(ResourceManager.instance().getTexture("characters_32b.png"), 32);
	}

	public static Animation<TextureRegion> witchWalk() {
		return new CharactersSheet32().WITCH_WALK_ANIMATION;
	}

	public static Animation<TextureRegion> witchAttack() {
		return new CharactersSheet32().WITCH_ATTACK_ANIMATION;
	}

	public static Animation<TextureRegion> witchIdle() {
		return new CharactersSheet32().WITCH_IDLE_ANIMATION;
	}

	public static Animation<TextureRegion> assassinWalk() {
		return new CharactersSheet32().ASSASSIN_WALK_ANIMATION;
	}

	public static Animation<TextureRegion> assasinAttack() {
		return new CharactersSheet32().ASSASSIN_ATTACK_ANIMATION;
	}

	public static Animation<TextureRegion> assassinIdle() {
		return new CharactersSheet32().ASSASSIN_IDLE_ANIMATION;
	}

	public static Animation<TextureRegion> thiefWalk() {
		return new CharactersSheet32().THIEF_WALK_ANIMATION;
	}

	public static Animation<TextureRegion> thiefAttack() {
		return new CharactersSheet32().THIEF_ATTACK_ANIMATION;
	}

	public static Animation<TextureRegion> thiefIdle() {
		return new CharactersSheet32().THIEF_IDLE_ANIMATION;
	}

}
