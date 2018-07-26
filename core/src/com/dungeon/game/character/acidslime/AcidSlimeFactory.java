package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.character.slime.SlimeFactory;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class AcidSlimeFactory implements EntityFactory.EntityTypeFactory {

	private static final String IDLE = "slime_acid_idle";
	private static final String ATTACK = "slime_acid_attack";

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> poolDryAnimation;

	final EntityPrototype character;
	final EntityPrototype death;
	final EntityPrototype pool;
	final EntityPrototype blob;
	final EntityPrototype splat;

	final float maxTargetDistance;
	final float dashDistance;
	final float poolSeparation;
	final float poolDamage;
	final float attackFrequency;
	final float damagePerSecond;

	public AcidSlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("SLIME_ACID");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		dashDistance = Util.length2(config.getLong("dashDistance", 150L));
		poolSeparation = Util.length2(config.getLong("poolSeparation", 15L));
		poolDamage = config.getLong("poolDamage", 5L);
		attackFrequency = config.getDouble("attackFrequency", 3d).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();

		// Character animations
		idleAnimation = ResourceManager.getAnimation(IDLE);
		attackAnimation = ResourceManager.getAnimation(ATTACK);
		// Pool animations
		poolDryAnimation = ResourceManager.getAnimation(SlimeFactory.POOL_DRY);

		character = ResourceManager.getPrototype("creature_slime_acid");
		death = ResourceManager.getPrototype("creature_slime_acid_death");
		pool = ResourceManager.getPrototype("creature_slime_acid_pool");
		blob = ResourceManager.getPrototype("creature_slime_acid_blob");
		//.zSpeed(() -> Rand.between(50f, 100f))
		splat = ResourceManager.getPrototype("creature_slime_acid_splat");
	}

	@Override
	public Entity build(Vector2 origin) {
		return new AcidSlime(origin, this);
	}

	Entity createDeath(Entity dying) {
		Entity entity = new Entity(dying.getPos(), death);
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		return entity;
	}

	Entity createPool(Entity dying) {
		Entity entity = new AcidPool(dying.getPos(), this);
		entity.setZPos(dying.getZPos());
		return entity;
	}

	Entity createBlob(Entity dying) {
		Entity entity = new Entity(dying.getPos(), blob) {
			@Override
			protected void onExpire() {
				Entity splatEntity = new Entity(getPos(), splat);
				GameState.addEntity(splatEntity);
			}
			@Override
			protected void onGroundRest() {
				expire();
			}
		};
		entity.setZPos(8);
		entity.impulse(Rand.between(-50f, 50f), Rand.between(-10f, 10f));
		return entity;
	}

}
