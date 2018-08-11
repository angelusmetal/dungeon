package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.NewEntityTypeFactory;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class AssassinFactory implements NewEntityTypeFactory {

	public static final String ASSASSIN_WALK = "assassin_walk";
	private static final String ASSASSIN_ATTACK = "assassin_attack";
	private static final String ASSASSIN_IDLE = "assassin_idle";

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;

	final EntityPrototype character;

	public AssassinFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("ASSASSIN");
		int health = config.getLong("health", 150L).intValue();
		float speed = config.getLong("speed", 60L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();

		attackAnimation = Resources.animations.get(ASSASSIN_ATTACK);
		idleAnimation = Resources.animations.get(ASSASSIN_IDLE);
		walkAnimation = Resources.animations.get(ASSASSIN_WALK);

		Vector2 characterBoundingBox = new Vector2(14, 22);
		Vector2 characterDrawOffset = new Vector2(16, 13);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.health(health)
				.speed(speed)
				.knockback(0.5f)
				.friction(friction)
				.castsShadow(true);
	}


	public Assassin build(Vector2 origin) {
		return new Assassin(origin, this);
	}

}
