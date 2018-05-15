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
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.character.slime.SlimeBlobsSheet;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class AcidSlimeFactory implements EntityFactory.EntityTypeFactory {

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

	public AcidSlimeFactory() {
		Toml config = GameState.getConfiguration().getTable("creatures.SLIME_ACID");
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
		idleAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.IDLE, AcidSlimeSheet::idle);
		attackAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.ATTACK, AcidSlimeSheet::attack);
		dieAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.DIE, AcidSlimeSheet::die);
		// Pool animations
		poolFloodAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.POOL_FLOOD, SlimeBlobsSheet::poolFlood);
		poolDryAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.POOL_DRY, SlimeBlobsSheet::poolDry);
		// Blob animations
		blobAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.BLOB, SlimeBlobsSheet::blob);
		splatAnimation = ResourceManager.instance().getAnimation(SlimeBlobsSheet.SPLAT, SlimeBlobsSheet::splat);

		Color color = new Color(0, 1, 0, 0.5f);
		Color lightColor = new Color(0, 1, 0, 0.5f);

		Light characterLight = new Light(100, lightColor, Light.RAYS_TEXTURE, () -> 1f, Light::rotateMedium);
		Light deathLight = new Light(150, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light poolLight = new Light(100, lightColor, Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);
		Vector2 poolBoundingBox = new Vector2(22, 12);
		Vector2 poolDrawOffset = new Vector2(16, 3);
		Vector2 blobBouncingBox = new Vector2(6, 6);
		Vector2 blobDrawOffset = new Vector2(8, 8);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(color)
				.light(characterLight)
				.speed(speed)
				.friction(friction)
				.health(() -> health * (GameState.getPlayerCount() + GameState.getLevelCount()));
		death = new EntityPrototype()
				.animation(dieAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.timeToLive(dieAnimation.getAnimationDuration() + 1f)
				.light(deathLight);
		pool = new EntityPrototype()
				.animation(poolFloodAnimation)
				.boundingBox(poolBoundingBox)
				.drawOffset(poolDrawOffset)
				.color(color)
				.light(poolLight)
				.timeToLive(poolDuration)
				.with(Traits.fadeOutLight())
				.zIndex(-1);
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
		entity.setColor(dying.getColor());
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
