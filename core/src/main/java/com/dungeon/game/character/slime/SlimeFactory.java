package com.dungeon.game.character.slime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

import java.util.function.Supplier;

public class SlimeFactory implements EntityFactory.EntityTypeFactory {

	private static final String IDLE = "slime_idle";
	private static final String BLINK = "slime_blink";

	private static final String SPAWN_IDLE = "slime_mini_idle";
	private static final String SPAWN_BLINK = "slime_mini_blink";

	public static final String POOL_DRY = "slime_pool_dry";

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> blinkAnimation;
	final Animation<TextureRegion> spawnIdleAnimation;
	final Animation<TextureRegion> spawnBlinkAnimation;

	final EntityPrototype character;
	final EntityPrototype spawn;
	final EntityPrototype death;
	final EntityPrototype spawnDeath;
	final EntityPrototype blob;
	final EntityPrototype splat;

	final float maxTargetDistance;
	final float jumpDistance;
	final float damagePerSecond;
	final int blobsOnDeath;
	final float attackFrequency;

	public SlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("SLIME");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		jumpDistance = Util.length2(config.getLong("jumpDistance", 50L));
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();
		blobsOnDeath = config.getLong("blobsOnDeath", 16L).intValue();
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

		character = Resources.prototypes.get("creature_slime")
				.color(color);
		death = Resources.prototypes.get("creature_slime_death");
		spawn = Resources.prototypes.get("creature_slime_spawn");
		spawnDeath = Resources.prototypes.get("creature_slime_spawn_death");
		blob = Resources.prototypes.get("creature_slime_blob");
		splat = Resources.prototypes.get("creature_slime_splat");
	}

	@Override
	public Entity build(Vector2 origin) {
		Slime slime = new Slime(origin, this);
		slime.getLight().color.set(slime.getColor());
		return slime;
	}

	Entity createSpawn(Entity dying) {
		Entity entity = new SlimeSpawn(dying.getOrigin(), this);
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		entity.impulse(Rand.between(-20f, 20f), Rand.between(-10f, 10f));
		return entity;
	}

	Entity createDeath(Entity dying) {
		Entity entity = new Entity(death, dying.getOrigin());
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		return entity;
	}

	Entity createSpawnDeath(Entity dying) {
		Entity entity = new Entity(spawnDeath, dying.getOrigin());
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		return entity;
	}

	Entity createBlob(Entity dying) {
		Entity entity = new Entity(blob, dying.getOrigin()) {
			@Override
			protected void onExpire() {
				Entity splatEntity = new Entity(splat, getOrigin());
				splatEntity.setColor(dying.getColor());
				GameState.entities.add(splatEntity);
			}
			@Override
			protected void onGroundRest() {
				expire();
			}
		};
		entity.setZPos(8);
		entity.impulse(Rand.between(-50f, 50f), Rand.between(-10f, 10f));
		entity.setColor(dying.getColor());
		return entity;
	}
}
