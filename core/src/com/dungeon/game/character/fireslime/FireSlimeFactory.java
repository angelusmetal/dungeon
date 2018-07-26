package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.combat.FireballWeapon;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class FireSlimeFactory implements EntityFactory.EntityTypeFactory {

	private static final String IDLE = "slime_fire_idle";

	final Animation<TextureRegion> idleAnimation;

	final EntityPrototype character;

	final float maxTargetDistance;
	final float attackFrequency;
	final float attackSpeed;
	final float idleSpeed;
	final float damagePerSecond;

	final Weapon weapon;

	public FireSlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("SLIME_FIRE");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		attackFrequency = config.getDouble("attackFrequency", 1.5d).floatValue();
		attackSpeed = config.getLong("attackSpeed", 10L).floatValue();
		idleSpeed = config.getLong("idleSpeed", 5L).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();

		// Character animations
		idleAnimation = ResourceManager.getAnimation(IDLE);

		character = ResourceManager.getPrototype("creature_slime_fire");
		weapon = new FireballWeapon();
	}

	@Override
	public Entity build(Vector2 origin) {
		return new FireSlime(origin, this);
	}

	public Weapon getWeapon() {
		return weapon;
	}

}
