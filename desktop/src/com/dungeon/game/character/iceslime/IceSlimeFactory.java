package com.dungeon.game.character.iceslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.util.Util;
import com.dungeon.game.character.slime.SlimeFactory;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.resource.DungeonResources;

public class IceSlimeFactory {

	private static final String IDLE = "slime_ice_idle";
	private static final String HIT = "slime_ice_hit";

	final Animation<Sprite> idleAnimation;
	final Animation<Sprite> hitAnimation;

	final float maxTargetDistance = Util.length2(300f);
	final float dashDistance = Util.length2(150f);
	final float poolSeparation = Util.length2(15f);
	final float attackFrequency = 3f;
	final float damagePerSecond = 10f;

	public final EntityTypeFactory pool;

	public IceSlimeFactory() {
		// Character animations
		idleAnimation = Resources.animations.get(IDLE);
		hitAnimation = Resources.animations.get(HIT);

		final Animation<Sprite> poolDryAnimation = Resources.animations.get(SlimeFactory.POOL_DRY);
		final EntityPrototype pool = DungeonResources.prototypes.get("ice_pool");

		this.pool = origin -> new DungeonEntity(pool, origin) {
			@Override public void think() {
				if (Engine.time() > expirationTime - 0.5f && getAnimation() != poolDryAnimation) {
					startAnimation(poolDryAnimation);
				}
			}

//			@Override protected boolean onEntityCollision(DungeonEntity entity) {
//				if (entity instanceof PlayerEntity) {
//					Attack attack = new Attack(this, damagePerSecond * Engine.frameTime(), DamageType.ELEMENTAL, 0);
//					entity.hit(attack);
//					return true;
//				} else {
//					return false;
//				}
//			}
		};
	}

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new IceSlime(origin, prototype, this);
	}

}
