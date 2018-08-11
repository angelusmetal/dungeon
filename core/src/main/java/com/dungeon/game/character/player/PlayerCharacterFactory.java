package com.dungeon.game.character.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.NewEntityTypeFactory;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;

public class PlayerCharacterFactory {

	public static final String ASSASSIN_WALK = "assassin_walk";
	private static final String ASSASSIN_ATTACK = "assassin_attack";
	private static final String ASSASSIN_IDLE = "assassin_idle";

	public static final String THIEF_WALK = "thief_walk";
	private static final String THIEF_ATTACK = "thief_attack";
	private static final String THIEF_IDLE = "thief_idle";

	public static final String WITCH_WALK = "witch_walk";
	private static final String WITCH_ATTACK = "witch_attack";
	private static final String WITCH_IDLE = "witch_idle";

	public final NewEntityTypeFactory assassin;
	public final NewEntityTypeFactory thief;
	public final NewEntityTypeFactory witch;

	public PlayerCharacterFactory() {
		assassin = factory(ASSASSIN_IDLE, ASSASSIN_WALK, ASSASSIN_ATTACK, "player_assassin");
		thief = factory(THIEF_IDLE, THIEF_WALK, THIEF_ATTACK, "player_thief");
		witch = factory(WITCH_IDLE, WITCH_WALK, WITCH_ATTACK, "player_witch");
	}

	private NewEntityTypeFactory factory(String idle, String walk, String attack, String prototype) {
		final Animation<TextureRegion> idleAnimation = Resources.animations.get(idle);
		final Animation<TextureRegion> walkAnimation = Resources.animations.get(walk);
		final Animation<TextureRegion> attackAnimation = Resources.animations.get(attack);
		final EntityPrototype character = Resources.prototypes.get(prototype);

		return origin -> new PlayerEntity(character, origin) {
			@Override protected Animation<TextureRegion> getAttackAnimation() { return attackAnimation; }
			@Override protected Animation<TextureRegion> getIdleAnimation() { return idleAnimation; }
			@Override protected Animation<TextureRegion> getWalkAnimation() { return walkAnimation; }
			@Override protected void onExpire() {
				super.onExpire();
				GameState.entities.add(GameState.build(EntityType.TOMBSTONE, getOrigin()));
			}
		};

	}


}
