package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.resource.DungeonResources;
import com.moandjiezana.toml.Toml;

import java.util.function.Supplier;

public class SlimeFactory {

	private static final String IDLE = "slime_idle";
	private static final String BLINK = "slime_blink";

	private static final String SPAWN_IDLE = "slime_mini_idle";
	private static final String SPAWN_BLINK = "slime_mini_blink";

	public static final String POOL_DRY = "slime_pool_dry";

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> blinkAnimation;
	final Animation<TextureRegion> spawnIdleAnimation;
	final Animation<TextureRegion> spawnBlinkAnimation;

	final float maxTargetDistance;
	final float jumpDistance;
	final float damagePerSecond;
	final float attackFrequency;

	public SlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(Game.getConfiguration(), "creatures", "id").get("SLIME");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		jumpDistance = Util.length2(config.getLong("jumpDistance", 50L));
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();
		attackFrequency = config.getDouble("attackFrequency", 3d).floatValue();

		// Character animations
		idleAnimation = Resources.animations.get(IDLE);
		blinkAnimation = Resources.animations.get(BLINK);
		// Spawn animations
		spawnIdleAnimation = Resources.animations.get(SPAWN_IDLE);
		spawnBlinkAnimation = Resources.animations.get(SPAWN_BLINK);

		Supplier<Color> color = () -> Util.hsvaToColor(
				Rand.between(0f, 1f),
				1f,
				Rand.between(0.7f, 1f),
				0.7f);

		// Setting up random color (TODO move this to the conf file)
		DungeonResources.prototypes.get("creature_slime")
				.color(color);
	}

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		Slime slime = new Slime(origin, prototype, this);
		slime.getLight().color.set(slime.getColor());
		return slime;
	}

	public Entity buildSpawn(Vector2 origin, EntityPrototype prototype) {
		return new SlimeSpawn(origin, prototype, this);
	}

}
