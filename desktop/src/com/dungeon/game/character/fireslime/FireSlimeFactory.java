package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.Game;
import com.dungeon.game.combat.FireballWeapon;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.player.Players;
import com.dungeon.game.resource.DungeonResources;
import com.moandjiezana.toml.Toml;

public class FireSlimeFactory {

	private static final String IDLE = "slime_fire_idle";

	final Animation<TextureRegion> idleAnimation;

	final float maxTargetDistance;
	final float attackFrequency;
	final float attackSpeed;
	final float idleSpeed;
	final float damagePerSecond;

	final Weapon weapon;
	final Weapon bossWeapon;

	public FireSlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(Game.getConfiguration(), "creatures", "id").get("SLIME_FIRE");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		attackFrequency = config.getDouble("attackFrequency", 1.5d).floatValue();
		attackSpeed = config.getLong("attackSpeed", 10L).floatValue();
		idleSpeed = config.getLong("idleSpeed", 5L).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();

		// Character animations
		idleAnimation = Resources.animations.get(IDLE);

		weapon = new FireballWeapon();
		bossWeapon = new FireballWeapon(10);
	}

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new FireSlime(origin, prototype, this);
	}

	public Entity buildBoss(Vector2 origin, EntityPrototype prototype) {
		return new FireSlimeBoss(origin, prototype, this);
	}

	public Entity buildExplosion(Vector2 origin, EntityPrototype prototype) {
		return new Entity(prototype, origin) {
			@Override
			protected void onExpire() {
				int bullets = (Players.count() + Game.getLevelCount()) * 2;
				Vector2 aim = new Vector2(0, 1);
				for (int i = 0; i < bullets; ++i) {
					getWeapon().spawnEntities(getOrigin(), aim);
					aim.rotate(360f / bullets);
				}
			}
		};
	}
	public Weapon getWeapon() {
		return weapon;
	}

}
