package com.dungeon.game.character.slime;

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
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

import java.util.function.Supplier;

public class SlimeFactory implements EntityFactory.EntityTypeFactory {

	private static final String IDLE = "slime_idle";
	private static final String BLINK = "slime_blink";
	private static final String DIE = "slime_die";

	private static final String SPAWN_IDLE = "slime_mini_idle";
	private static final String SPAWN_BLINK = "slime_mini_blink";
	private static final String SPAWN_DIE = "slime_mini_die";

	public static final String POOL_FLOOD = "slime_pool_flood";
	public static final String POOL_DRY = "slime_pool_dry";
	public static final String BLOB = "slime_blob";
	public static final String SPLAT = "slime_splat";

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> blinkAnimation;
	final Animation<TextureRegion> dieAnimation;
	final Animation<TextureRegion> blobAnimation;
	final Animation<TextureRegion> splatAnimation;
	final Animation<TextureRegion> spawnIdleAnimation;
	final Animation<TextureRegion> spawnBlinkAnimation;
	final Animation<TextureRegion> spawnDieAnimation;

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
		int health = config.getLong("health", 75L).intValue();
		int spawnHealth = config.getLong("spawnHealth", 25L).intValue();
		float speed = config.getLong("speed", 100L).floatValue();
		float friction = config.getLong("friction", 1L).floatValue();

		// Character animations
		idleAnimation = ResourceManager.getAnimation(IDLE);
		blinkAnimation = ResourceManager.getAnimation(BLINK);
		dieAnimation = ResourceManager.getAnimation(DIE);
		// Spawn animations
		spawnIdleAnimation = ResourceManager.getAnimation(SPAWN_IDLE);
		spawnBlinkAnimation = ResourceManager.getAnimation(SPAWN_BLINK);
		spawnDieAnimation = ResourceManager.getAnimation(SPAWN_DIE);
		// Blob animations
		blobAnimation = ResourceManager.getAnimation(BLOB);
		splatAnimation = ResourceManager.getAnimation(SPLAT);

		Supplier<Color> color = () -> Util.hsvaToColor(
				Rand.between(0f, 1f),
				1f,
				Rand.between(0.7f, 1f),
				0.7f);

		Color lightColor = new Color(1, 1, 1, 0.5f);

		Light characterLight = new Light(50, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light spawnLight = new Light(25, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light deathLight = new Light(100, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);
		Vector2 spawnBoundingBox = new Vector2(11, 6);
		Vector2 spawnDrawOffset = new Vector2(8, 5);
		Vector2 blobBouncingBox = new Vector2(6, 6);
		Vector2 blobDrawOffset = new Vector2(8, 8);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(color)
				.light(characterLight)
				.speed(speed)
				.zSpeed(0)
				.with(Traits.zAccel(-200))
				.knockback(1f)
				.friction(friction)
				.health(() -> (int) (health * GameState.getDifficultyTier()));
		spawn = new EntityPrototype()
				.boundingBox(spawnBoundingBox)
				.drawOffset(spawnDrawOffset)
				.color(color)
				.light(spawnLight)
				.speed(speed)
				.zSpeed(0)
				.with(Traits.zAccel(-200))
				.knockback(1f)
				.friction(friction)
				.health(() -> (int) (spawnHealth * GameState.getDifficultyTier()));
		death = new EntityPrototype()
				.animation(dieAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(new Color(1, 1, 1, 0.5f))
				.light(deathLight)
				.with(Traits.fadeOutLight())
				.timeToLive(dieAnimation.getAnimationDuration() + 1f);
		spawnDeath = new EntityPrototype()
				.animation(spawnDieAnimation)
				.boundingBox(spawnBoundingBox)
				.drawOffset(spawnDrawOffset)
				.color(new Color(1, 1, 1, 0.5f))
				.light(deathLight)
				.with(Traits.fadeOutLight())
				.timeToLive(dieAnimation.getAnimationDuration() + 1f);
		blob = new EntityPrototype()
				.animation(blobAnimation)
				.boundingBox(blobBouncingBox)
				.drawOffset(blobDrawOffset)
				.speed(50)
				.zSpeed(() -> Rand.between(50f, 100f))
				.with(Traits.zAccel(-200))
				.timeToLive(10);
		splat = new EntityPrototype()
				.animation(splatAnimation)
				.boundingBox(blobBouncingBox)
				.drawOffset(blobDrawOffset)
				.timeToLive(splatAnimation.getAnimationDuration());
	}

	@Override
	public Entity build(Vector2 origin) {
		Slime slime = new Slime(origin, this);
		slime.getLight().color.set(slime.getColor());
		return slime;
	}

	Entity createSpawn(Entity dying) {
		Entity entity = new SlimeSpawn(dying.getPos(), this);
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		entity.impulse(Rand.between(-50f, 50f), Rand.between(-10f, 10f));
		return entity;
	}

	Entity createDeath(Entity dying) {
		Entity entity = new Entity(dying.getPos(), death);
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		return entity;
	}

	Entity createSpawnDeath(Entity dying) {
		Entity entity = new Entity(dying.getPos(), spawnDeath);
		entity.setZPos(dying.getZPos());
		entity.setColor(dying.getColor());
		entity.getLight().color.set(dying.getColor());
		return entity;
	}

	Entity createBlob(Entity dying) {
		Entity entity = new Entity(dying.getPos(), blob) {
			@Override
			protected void onExpire() {
				Entity splatEntity = new Entity(getPos(), splat);
				splatEntity.setColor(dying.getColor());
				GameState.addEntity(splatEntity);
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
