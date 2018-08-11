package com.dungeon.game.character.ghost;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.NewEntityTypeFactory;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class GhostFactory implements NewEntityTypeFactory {

	private static final String HOVER = "ghost_hover";

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

		idleAnimation = Resources.animations.get(HOVER);

		character = Resources.prototypes.get("creature_ghost");
		death = Resources.prototypes.get("creature_ghost_death");
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Ghost(origin, this);
	}

	public Entity createDeath(Vector2 origin, boolean invertX) {
		Entity entity = new Entity(death, origin);
		entity.setInvertX(invertX);
		return entity;
	}
}
