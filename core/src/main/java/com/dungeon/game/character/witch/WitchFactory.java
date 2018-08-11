package com.dungeon.game.character.witch;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.NewEntityTypeFactory;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class WitchFactory implements NewEntityTypeFactory {

	public static final String WITCH_WALK = "witch_walk";
	private static final String WITCH_ATTACK = "witch_attack";
	private static final String WITCH_IDLE = "witch_idle";

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;

	final EntityPrototype character;

	public WitchFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("WITCH");
		int health = config.getLong("health", 100L).intValue();
		float speed = config.getLong("speed", 60L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();

		idleAnimation = Resources.animations.get(WITCH_IDLE);
		walkAnimation = Resources.animations.get(WITCH_WALK);
		attackAnimation = Resources.animations.get(WITCH_ATTACK);

		Vector2 characterBoundingBox = new Vector2(14, 12);
		Vector2 characterDrawOffset = new Vector2(16, 6);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.health(health)
				.knockback(0.5f)
				.speed(speed)
				.friction(friction)
				.castsShadow(true);
	}

	public Witch build(Vector2 origin) {
		return new Witch(origin, this);
	}

}
