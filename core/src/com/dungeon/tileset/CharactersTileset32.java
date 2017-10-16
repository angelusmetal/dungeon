package com.dungeon.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.tileset.Tileset;

public class CharactersTileset32 extends Tileset {

	public final TextureRegion MONK_WALK_1 = getTile(0, 0);
	public final TextureRegion MONK_WALK_2 = getTile(1, 0);
	public final TextureRegion MONK_WALK_3 = getTile(2, 0);
	public final TextureRegion MONK_WALK_4 = getTile(3, 0);
	public final TextureRegion MONK_JUMP_1 = getTile(4, 0);
	public final TextureRegion MONK_JUMP_2 = getTile(5, 0);
	public final TextureRegion MONK_JUMP_3 = getTile(6, 0);
	public final TextureRegion MONK_JUMP_4 = getTile(7, 0);
	public final TextureRegion MONK_HIT_1 = getTile(8, 0);
	public final TextureRegion MONK_HIT_2 = getTile(9, 0);
	public final TextureRegion MONK_SLASH_1 = getTile(10, 0);
	public final TextureRegion MONK_SLASH_2 = getTile(11, 0);
	public final TextureRegion MONK_SLASH_3 = getTile(12, 0);
	public final TextureRegion MONK_PUNCH_1 = getTile(13, 0);
	public final TextureRegion MONK_RUN_1 = getTile(14, 0);
	public final TextureRegion MONK_RUN_2 = getTile(15, 0);
	public final TextureRegion MONK_RUN_3 = getTile(16, 0);
	public final TextureRegion MONK_RUN_4 = getTile(17, 0);
	public final TextureRegion MONK_CLIMB_1 = getTile(18, 0);
	public final TextureRegion MONK_CLIMB_2 = getTile(19, 0);
	public final TextureRegion MONK_CLIMB_3 = getTile(20, 0);
	public final TextureRegion MONK_CLIMB_4 = getTile(21, 0);

	public final TextureRegion KING_WALK_1 = getTile(0, 1);
	public final TextureRegion KING_WALK_2 = getTile(1, 1);
	public final TextureRegion KING_WALK_3 = getTile(2, 1);
	public final TextureRegion KING_WALK_4 = getTile(3, 1);
	public final TextureRegion KING_JUMP_1 = getTile(4, 1);
	public final TextureRegion KING_JUMP_2 = getTile(5, 1);
	public final TextureRegion KING_JUMP_3 = getTile(6, 1);
	public final TextureRegion KING_JUMP_4 = getTile(7, 1);
	public final TextureRegion KING_HIT_1 = getTile(8, 1);
	public final TextureRegion KING_HIT_2 = getTile(9, 1);
	public final TextureRegion KING_SLASH_1 = getTile(10, 1);
	public final TextureRegion KING_SLASH_2 = getTile(11, 1);
	public final TextureRegion KING_SLASH_3 = getTile(12, 1);
	public final TextureRegion KING_PUNCH_1 = getTile(13, 1);
	public final TextureRegion KING_RUN_1 = getTile(14, 1);
	public final TextureRegion KING_RUN_2 = getTile(15, 1);
	public final TextureRegion KING_RUN_3 = getTile(16, 1);
	public final TextureRegion KING_RUN_4 = getTile(17, 1);
	public final TextureRegion KING_CLIMB_1 = getTile(18, 1);
	public final TextureRegion KING_CLIMB_2 = getTile(19, 1);
	public final TextureRegion KING_CLIMB_3 = getTile(20, 1);
	public final TextureRegion KING_CLIMB_4 = getTile(21, 1);

	public final TextureRegion THIEF_WALK_1 = getTile(0, 2);
	public final TextureRegion THIEF_WALK_2 = getTile(1, 2);
	public final TextureRegion THIEF_WALK_3 = getTile(2, 2);
	public final TextureRegion THIEF_WALK_4 = getTile(3, 2);
	public final TextureRegion THIEF_JUMP_1 = getTile(4, 2);
	public final TextureRegion THIEF_JUMP_2 = getTile(5, 2);
	public final TextureRegion THIEF_JUMP_3 = getTile(6, 2);
	public final TextureRegion THIEF_JUMP_4 = getTile(7, 2);
	public final TextureRegion THIEF_HIT_1 = getTile(8, 2);
	public final TextureRegion THIEF_HIT_2 = getTile(9, 2);
	public final TextureRegion THIEF_SLASH_1 = getTile(10, 2);
	public final TextureRegion THIEF_SLASH_2 = getTile(11, 2);
	public final TextureRegion THIEF_SLASH_3 = getTile(12, 2);
	public final TextureRegion THIEF_PUNCH_1 = getTile(13, 2);
	public final TextureRegion THIEF_RUN_1 = getTile(14, 2);
	public final TextureRegion THIEF_RUN_2 = getTile(15, 2);
	public final TextureRegion THIEF_RUN_3 = getTile(16, 2);
	public final TextureRegion THIEF_RUN_4 = getTile(17, 2);
	public final TextureRegion THIEF_CLIMB_1 = getTile(18, 2);
	public final TextureRegion THIEF_CLIMB_2 = getTile(19, 2);
	public final TextureRegion THIEF_CLIMB_3 = getTile(20, 2);
	public final TextureRegion THIEF_CLIMB_4 = getTile(21, 2);

	public final Animation<TextureRegion> MONK_WALK_ANIMATION = loop(0.1f, MONK_WALK_1, MONK_WALK_2, MONK_WALK_3, MONK_WALK_4);
	public final Animation<TextureRegion> MONK_JUMP_ANIMATION = new Animation<>(0.1f, MONK_JUMP_1, MONK_JUMP_2, MONK_JUMP_3, MONK_JUMP_4);
	public final Animation<TextureRegion> MONK_HIT_ANIMATION = new Animation<>(0.1f, MONK_HIT_1, MONK_HIT_2, MONK_HIT_1);
	public final Animation<TextureRegion> MONK_SLASH_ANIMATION = new Animation<>(0.1f, MONK_SLASH_1, MONK_SLASH_2, MONK_SLASH_1, MONK_SLASH_3);
	public final Animation<TextureRegion> MONK_PUNCH_ANIMATION = new Animation<>(0.1f, MONK_PUNCH_1, MONK_SLASH_1);
	public final Animation<TextureRegion> MONK_RUN_ANIMATION = new Animation<>(0.1f, MONK_RUN_1, MONK_RUN_2, MONK_RUN_3, MONK_RUN_4);
	public final Animation<TextureRegion> MONK_CLIMB_ANIMATION = new Animation<>(0.1f, MONK_CLIMB_1, MONK_CLIMB_2, MONK_CLIMB_3, MONK_CLIMB_4);
	public final Animation<TextureRegion> MONK_IDLE_ANIMATION = loop(0.5f, MONK_WALK_1, MONK_JUMP_3);

	public final Animation<TextureRegion> KING_WALK_ANIMATION = loop(0.1f, KING_WALK_1, KING_WALK_2, KING_WALK_3, KING_WALK_4);
	public final Animation<TextureRegion> KING_JUMP_ANIMATION = new Animation<>(0.1f, KING_JUMP_1, KING_JUMP_2, KING_JUMP_3, KING_JUMP_4);
	public final Animation<TextureRegion> KING_HIT_ANIMATION = new Animation<>(0.1f, KING_HIT_1, KING_HIT_2, KING_HIT_1);
	public final Animation<TextureRegion> KING_SLASH_ANIMATION = new Animation<>(0.1f, KING_SLASH_1, KING_SLASH_2, KING_SLASH_1, KING_SLASH_3);
	public final Animation<TextureRegion> KING_PUNCH_ANIMATION = new Animation<>(0.1f, KING_PUNCH_1, KING_SLASH_1);
	public final Animation<TextureRegion> KING_RUN_ANIMATION = new Animation<>(0.1f, KING_RUN_1, KING_RUN_2, KING_RUN_3, KING_RUN_4);
	public final Animation<TextureRegion> KING_CLIMB_ANIMATION = new Animation<>(0.1f, KING_CLIMB_1, KING_CLIMB_2, KING_CLIMB_3, KING_CLIMB_4);
	public final Animation<TextureRegion> KING_IDLE_ANIMATION = loop(0.5f, KING_WALK_1, KING_JUMP_3);

	public final Animation<TextureRegion> THIEF_WALK_ANIMATION = loop(0.1f, THIEF_WALK_1, THIEF_WALK_2, THIEF_WALK_3, THIEF_WALK_4);
	public final Animation<TextureRegion> THIEF_JUMP_ANIMATION = new Animation<>(0.1f, THIEF_JUMP_1, THIEF_JUMP_2, THIEF_JUMP_3, THIEF_JUMP_4);
	public final Animation<TextureRegion> THIEF_HIT_ANIMATION = new Animation<>(0.1f, THIEF_HIT_1, THIEF_HIT_2, THIEF_HIT_1);
	public final Animation<TextureRegion> THIEF_SLASH_ANIMATION = new Animation<>(0.1f, THIEF_SLASH_1, THIEF_SLASH_2, THIEF_SLASH_1, THIEF_SLASH_3);
	public final Animation<TextureRegion> THIEF_PUNCH_ANIMATION = new Animation<>(0.1f, THIEF_PUNCH_1, THIEF_SLASH_1);
	public final Animation<TextureRegion> THIEF_RUN_ANIMATION = new Animation<>(0.1f, THIEF_RUN_1, THIEF_RUN_2, THIEF_RUN_3, THIEF_RUN_4);
	public final Animation<TextureRegion> THIEF_CLIMB_ANIMATION = new Animation<>(0.1f, THIEF_CLIMB_1, THIEF_CLIMB_2, THIEF_CLIMB_3, THIEF_CLIMB_4);
	public final Animation<TextureRegion> THIEF_IDLE_ANIMATION = loop(0.5f, THIEF_WALK_1, THIEF_JUMP_3);

	public CharactersTileset32() {
		super(new Texture("characters_32.png"), 32);
		KING_WALK_ANIMATION.setPlayMode(Animation.PlayMode.LOOP);
	}

}
