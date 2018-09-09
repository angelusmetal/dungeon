package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.character.slime.SlimeFactory;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.resource.Resources;
import com.dungeon.engine.Engine;
import com.moandjiezana.toml.Toml;

public class AcidSlimeFactory {

	private static final String IDLE = "slime_acid_idle";
	private static final String ATTACK = "slime_acid_attack";

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;

	final float maxTargetDistance;
	final float dashDistance;
	final float poolSeparation;
	final float poolDamage;
	final float attackFrequency;
	final float damagePerSecond;

	public final EntityTypeFactory pool;

	public AcidSlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(Game.getConfiguration(), "creatures", "id").get("SLIME_ACID");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		dashDistance = Util.length2(config.getLong("dashDistance", 150L));
		poolSeparation = Util.length2(config.getLong("poolSeparation", 15L));
		poolDamage = config.getLong("poolDamage", 5L);
		attackFrequency = config.getDouble("attackFrequency", 3d).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();

		// Character animations
		idleAnimation = Resources.animations.get(IDLE);
		attackAnimation = Resources.animations.get(ATTACK);

		final Animation<TextureRegion> poolDryAnimation = Resources.animations.get(SlimeFactory.POOL_DRY);
		final EntityPrototype pool = Resources.prototypes.get("creature_slime_acid_pool");

		this.pool = origin -> new DungeonEntity(pool, origin) {
			@Override public void think() {
				if (Engine.time() > expirationTime - 0.5f && getCurrentAnimation().getAnimation() != poolDryAnimation) {
					setCurrentAnimation(poolDryAnimation);
				}
			}

			@Override protected boolean onEntityCollision(DungeonEntity entity) {
				if (entity instanceof PlayerEntity) {
					Attack attack = new Attack(this, damagePerSecond * Engine.frameTime(), DamageType.ELEMENTAL, 0);
					entity.hit(attack);
					return true;
				} else {
					return false;
				}
			}
		};
	}

	public Entity build(Vector2 origin, EntityPrototype prototype) {
		return new AcidSlime(origin, prototype, this);
	}

}
