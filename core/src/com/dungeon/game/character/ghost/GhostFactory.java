package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class GhostFactory implements EntityFactory.EntityTypeFactory {

	public static final String HOVER = "ghost_hover";

	final Animation<TextureRegion> idleAnimation;
	final EntityPrototype character;
	final EntityPrototype death;

	final float maxTargetDistance;
	final float visibleTime;
	final float visibleSpeed;
	final float stealthSpeed;
	final float damagePerSecond;

	public GhostFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("GHOST");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		visibleTime = config.getDouble("visibleTime", 2d).floatValue();
		visibleSpeed = config.getLong("visibleSpeed", 20L).floatValue();
		stealthSpeed = config.getLong("stealthSpeed", 40L).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 20L).floatValue();
		int health = config.getLong("health", 100L).intValue();
		float speed = config.getLong("speed", 20L).floatValue();

		idleAnimation = ResourceManager.getAnimation(HOVER);

		Vector2 boundingBox = new Vector2(16, 26);
		Vector2 drawOffset = new Vector2(16, 16);

		Light characterLight = new Light(200, new Color(0.2f, 0.4f, 1, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateSlow);
		character = new EntityPrototype()
				.boundingBox(boundingBox)
				.drawOffset(drawOffset)
				.color(new Color(1, 1, 1, 0.5f))
				.light(characterLight)
				.speed(speed)
				.health(() -> health * (GameState.getPlayerCount() + GameState.getLevelCount()));
		death = new EntityPrototype()
				.animation(idleAnimation)
				.boundingBox(boundingBox)
				.drawOffset(drawOffset)
				.color(new Color(1, 0, 0, 0.5f))
				.light(characterLight)
				.with(Traits.fadeOut(0.5f))
				.with(Traits.fadeOutLight())
				.timeToLive(1f)
				.zSpeed(50f);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Ghost(origin, this);
	}

	public Entity createDeath(Vector2 origin, boolean invertX) {
		Entity entity = new Entity(origin, death);
		entity.setInvertX(invertX);
		return entity;
	}
}
