package com.dungeon.game.character.acidslime;

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
import com.dungeon.game.character.slime.SlimeFactory;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class AcidSlimeFactory implements EntityFactory.EntityTypeFactory {

	private static final String IDLE = "slime_acid_idle";
	private static final String ATTACK = "slime_acid_attack";
	private static final String DIE = "slime_acid_die";

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> dieAnimation;
	final Animation<TextureRegion> poolFloodAnimation;
	final Animation<TextureRegion> poolDryAnimation;
	final Animation<TextureRegion> blobAnimation;
	final Animation<TextureRegion> splatAnimation;

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

	final Color lightColor = new Color(0xA3D957FF);

	public AcidSlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("SLIME_ACID");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		dashDistance = Util.length2(config.getLong("dashDistance", 150L));
		poolSeparation = Util.length2(config.getLong("poolSeparation", 15L));
		poolDamage = config.getLong("poolDamage", 5L);
		attackFrequency = config.getDouble("attackFrequency", 3d).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();
		int health = config.getLong("health", 100L).intValue();
		float speed = config.getLong("speed", 100L).floatValue();
		float friction = config.getLong("friction", 2L).floatValue();
		float poolDuration = config.getDouble("poolDuration", 5d).floatValue();

		// Character animations
		idleAnimation = ResourceManager.getAnimation(IDLE);
		attackAnimation = ResourceManager.getAnimation(ATTACK);
		dieAnimation = ResourceManager.getAnimation(DIE);
		// Pool animations
		poolFloodAnimation = ResourceManager.getAnimation(SlimeFactory.POOL_FLOOD);
		poolDryAnimation = ResourceManager.getAnimation(SlimeFactory.POOL_DRY);
		// Blob animations
		blobAnimation = ResourceManager.getAnimation(SlimeFactory.BLOB);
		splatAnimation = ResourceManager.getAnimation(SlimeFactory.SPLAT);

		Color color = new Color(1, 1, 1, 0.7f);

		Light characterLight = new Light(100, lightColor, Light.RAYS_TEXTURE, Light.rotateMedium());
		Light deathLight = new Light(150, lightColor, Light.NORMAL_TEXTURE);
		Light poolLight = new Light(100, lightColor, Light.NORMAL_TEXTURE);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);
		Vector2 poolBoundingBox = new Vector2(22, 12);
		Vector2 poolDrawOffset = new Vector2(16, 0);
		Vector2 blobBouncingBox = new Vector2(6, 6);
		Vector2 blobDrawOffset = new Vector2(8, 8);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(color)
				.light(characterLight)
				.speed(speed)
				.knockback(1f)
				.friction(friction)
				.health(() -> (int) (health * GameState.getDifficultyTier()));
		death = new EntityPrototype()
				.animation(dieAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.timeToLive(dieAnimation.getAnimationDuration() + 1f)
				.with(Traits.fadeOutLight())
				.light(deathLight);
		pool = new EntityPrototype()
				.animation(poolFloodAnimation)
				.boundingBox(poolBoundingBox)
				.drawOffset(poolDrawOffset)
				.color(lightColor)
				.light(poolLight)
				.timeToLive(poolDuration)
				.with(Traits.fadeOutLight())
				.zIndex(-1);
		blob = new EntityPrototype()
				.animation(blobAnimation)
				.boundingBox(blobBouncingBox)
				.color(lightColor)
				.drawOffset(blobDrawOffset)
				.speed(50)
				.zSpeed(() -> Rand.between(50f, 100f))
				.with(Traits.zAccel(-200))
				.timeToLive(10);
		splat = new EntityPrototype()
				.animation(splatAnimation)
				.boundingBox(blobBouncingBox)
				.color(lightColor)
				.drawOffset(blobDrawOffset)
				.timeToLive(splatAnimation.getAnimationDuration());
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
