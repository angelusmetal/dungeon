package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;

public class CharactersTileset32 extends Tileset {

	public final TextureRegion WITCH_WALK_1 = getTile(0, 0);
	public final TextureRegion WITCH_WALK_2 = getTile(1, 0);
	public final TextureRegion WITCH_WALK_3 = getTile(2, 0);
	public final TextureRegion WITCH_WALK_4 = getTile(3, 0);
	public final TextureRegion WITCH_WALK_5 = getTile(4, 0);
	public final TextureRegion WITCH_WALK_6 = getTile(5, 0);
	public final TextureRegion WITCH_WALK_7 = getTile(6, 0);
	public final TextureRegion WITCH_WALK_8 = getTile(7, 0);
	public final TextureRegion WITCH_WALK_9 = getTile(8, 0);
	public final TextureRegion WITCH_ATTACK_1 = getTile(9, 0);
	public final TextureRegion WITCH_ATTACK_2 = getTile(10, 0);
	public final TextureRegion WITCH_ATTACK_3 = getTile(11, 0);
	public final TextureRegion WITCH_ATTACK_4 = getTile(12, 0);
	public final TextureRegion WITCH_ATTACK_5 = getTile(13, 0);
	public final TextureRegion WITCH_ATTACK_6 = getTile(14, 0);
	public final TextureRegion WITCH_ATTACK_7 = getTile(15, 0);
	public final TextureRegion WITCH_ATTACK_8 = getTile(16, 0);
	public final TextureRegion WITCH_ATTACK_9 = getTile(17, 0);
	public final TextureRegion WITCH_ATTACK_10 = getTile(18, 0);
	public final TextureRegion WITCH_IDLE_1 = getTile(20, 0);
	public final TextureRegion WITCH_IDLE_2 = getTile(21, 0);
	public final TextureRegion WITCH_IDLE_3 = getTile(22, 0);

	public final TextureRegion ASSASIN_WALK_1 = getTile(0, 1);
	public final TextureRegion ASSASIN_WALK_2 = getTile(1, 1);
	public final TextureRegion ASSASIN_WALK_3 = getTile(2, 1);
	public final TextureRegion ASSASIN_WALK_4 = getTile(3, 1);
	public final TextureRegion ASSASIN_WALK_5 = getTile(4, 1);
	public final TextureRegion ASSASIN_WALK_6 = getTile(5, 1);
	public final TextureRegion ASSASIN_WALK_7 = getTile(6, 1);
	public final TextureRegion ASSASIN_WALK_8 = getTile(7, 1);
	public final TextureRegion ASSASIN_WALK_9 = getTile(8, 1);
	public final TextureRegion ASSASIN_ATTACK_1 = getTile(9, 1);
	public final TextureRegion ASSASIN_ATTACK_2 = getTile(10, 1);
	public final TextureRegion ASSASIN_ATTACK_3 = getTile(11, 1);
	public final TextureRegion ASSASIN_ATTACK_4 = getTile(12, 1);
	public final TextureRegion ASSASIN_ATTACK_5 = getTile(13, 1);
	public final TextureRegion ASSASIN_ATTACK_6 = getTile(14, 1);
	public final TextureRegion ASSASIN_ATTACK_7 = getTile(15, 1);
	public final TextureRegion ASSASIN_ATTACK_8 = getTile(16, 1);
	public final TextureRegion ASSASIN_ATTACK_9 = getTile(17, 1);
	public final TextureRegion ASSASIN_ATTACK_10 = getTile(18, 1);
	public final TextureRegion ASSASIN_IDLE_1 = getTile(20, 1);
	public final TextureRegion ASSASIN_IDLE_2 = getTile(21, 1);
	public final TextureRegion ASSASIN_IDLE_3 = getTile(22, 1);

	public final TextureRegion THIEF_WALK_1 = getTile(0, 2);
	public final TextureRegion THIEF_WALK_2 = getTile(1, 2);
	public final TextureRegion THIEF_WALK_3 = getTile(2, 2);
	public final TextureRegion THIEF_WALK_4 = getTile(3, 2);
	public final TextureRegion THIEF_ATTACK_1 = getTile(8, 2);
	public final TextureRegion THIEF_ATTACK_2 = getTile(9, 2);
	public final TextureRegion THIEF_IDLE_1 = getTile(0, 2);
	public final TextureRegion THIEF_IDLE_2 = getTile(6, 2);

	public final Animation<TextureRegion> WITCH_WALK_ANIMATION = loop(0.05f, WITCH_WALK_1, WITCH_WALK_2, WITCH_WALK_3, WITCH_WALK_4, WITCH_WALK_5, WITCH_WALK_6, WITCH_WALK_7, WITCH_WALK_8, WITCH_WALK_9);
	public final Animation<TextureRegion> WITCH_ATTACK_ANIMATION = new Animation<>(0.08f, WITCH_ATTACK_1, WITCH_ATTACK_2, WITCH_ATTACK_3, WITCH_ATTACK_4, WITCH_ATTACK_5, WITCH_ATTACK_6, WITCH_ATTACK_7, WITCH_ATTACK_8, WITCH_ATTACK_9, WITCH_ATTACK_10);
	public final Animation<TextureRegion> WITCH_IDLE_ANIMATION = loop(0.3f, WITCH_IDLE_1, WITCH_IDLE_2, WITCH_IDLE_3, WITCH_IDLE_2);

	public final Animation<TextureRegion> ASSASIN_WALK_ANIMATION = loop(0.05f, ASSASIN_WALK_1, ASSASIN_WALK_2, ASSASIN_WALK_3, ASSASIN_WALK_4, ASSASIN_WALK_5, ASSASIN_WALK_6/*, ASSASIN_WALK_7, ASSASIN_WALK_8, ASSASIN_WALK_9*/);
	public final Animation<TextureRegion> ASSASIN_ATTACK_ANIMATION = new Animation<>(0.02f, ASSASIN_ATTACK_1, ASSASIN_ATTACK_2, ASSASIN_ATTACK_3, ASSASIN_ATTACK_4, ASSASIN_ATTACK_5, ASSASIN_ATTACK_6, ASSASIN_ATTACK_7, ASSASIN_ATTACK_8, ASSASIN_ATTACK_9, ASSASIN_ATTACK_10);
	public final Animation<TextureRegion> ASSASIN_IDLE_ANIMATION = loop(0.3f, ASSASIN_IDLE_1, ASSASIN_IDLE_2, ASSASIN_IDLE_3, ASSASIN_IDLE_2);

	public final Animation<TextureRegion> THIEF_WALK_ANIMATION = loop(0.1f, THIEF_WALK_1, THIEF_WALK_2, THIEF_WALK_3, THIEF_WALK_4);
	public final Animation<TextureRegion> THIEF_ATTACK_ANIMATION = new Animation<>(0.1f, THIEF_ATTACK_1, THIEF_ATTACK_2, THIEF_ATTACK_1);
	public final Animation<TextureRegion> THIEF_IDLE_ANIMATION = loop(0.5f, THIEF_IDLE_1, THIEF_IDLE_2);

	public CharactersTileset32() {
		super(new Texture("characters_32b.png"), 32);
	}

}
