package com.dungeon.game.character.thief;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class ThiefFactory implements EntityFactory.EntityTypeFactory {

	public static final String THIEF_WALK = "thief_walk";
	private static final String THIEF_ATTACK = "thief_attack";
	private static final String THIEF_IDLE = "thief_idle";

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;

	final EntityPrototype character;

	public ThiefFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("THIEF");
		int health = config.getLong("health", 100L).intValue();
		float speed = config.getLong("speed", 96L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();

		idleAnimation = ResourceManager.getAnimation(THIEF_IDLE);
		walkAnimation = ResourceManager.getAnimation(THIEF_WALK);
		attackAnimation = ResourceManager.getAnimation(THIEF_ATTACK);

		Vector2 characterBoundingBox = new Vector2(14, 12);
		Vector2 characterDrawOffset = new Vector2(16, 6);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.health(health)
				.knockback(0.5f)
				.speed(speed)
				.friction(friction);
	}

	public Thief build(Vector2 origin) {
		return new Thief(origin, this);
	}

}
