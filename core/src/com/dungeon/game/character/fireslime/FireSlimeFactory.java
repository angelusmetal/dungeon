package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.util.Util;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class FireSlimeFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> projectileAnimation;
	final Animation<TextureRegion> explosionAnimation;

	final EntityPrototype character;
	final EntityPrototype bullet;
	final EntityPrototype bulletExplosion;
	final EntityPrototype bulletTrail;

	final Light characterLight;
	final Light bulletLight;
	final Light bulletTrailLight;

	final float maxTargetDistance;
	final float attackFrequency;
	final float attackSpeed;
	final float idleSpeed;
	final float damagePerSecond;

	public FireSlimeFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("SLIME_FIRE");
		maxTargetDistance = Util.length2(config.getLong("maxTargetDistance", 300L));
		attackFrequency = config.getDouble("attackFrequency", 1.5d).floatValue();
		attackSpeed = config.getLong("attackSpeed", 10L).floatValue();
		idleSpeed = config.getLong("idleSpeed", 5L).floatValue();
		damagePerSecond = config.getLong("damagePerSecond", 10L).floatValue();
		int health = config.getLong("health", 50L).intValue();
		float speed = config.getLong("speed", 20L).floatValue();
		float bulletSpeed = config.getLong("bulletSpeed", 100L).floatValue();
		float bulletDamage = config.getLong("bulletDamage", 5L).floatValue();

		// Character animations
		idleAnimation = ResourceManager.getAnimation(FireSlimeSheet.IDLE, FireSlimeSheet::idle);
		projectileAnimation = ResourceManager.getAnimation(FireSlimeSheet.PROJECTILE, FireSlimeSheet::projectile);
		explosionAnimation = ResourceManager.getAnimation(FireSlimeSheet.EXPLOSION, FireSlimeSheet::explosion);

		characterLight = new Light(100, new Color(1, 0.5f, 0, 0.8f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		bulletLight = new Light(50, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		bulletTrailLight = new Light(20, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

		Vector2 characterBoundingBox = new Vector2(22, 12);
		Vector2 characterDrawOffset = new Vector2(16, 11);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(8, 8);

		character = new EntityPrototype()
				.animation(idleAnimation)
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.color(new Color(1, 1, 1, 0.8f))
				.light(characterLight)
				.speed(speed)
				.health(() -> health * (GameState.getPlayerCount() + GameState.getLevelCount()));
		bullet = new EntityPrototype()
				.animation(projectileAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(bulletSpeed)
				.timeToLive(10)
				.hitPredicate(PlayerCharacter.HIT_PLAYERS)
				.damage(() -> bulletDamage * (GameState.getPlayerCount() + GameState.getLevelCount()))
				.with(Traits.generator(0.1f, this::createBulletTrail));
		bulletExplosion = new EntityPrototype()
				.animation(explosionAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(explosionAnimation.getAnimationDuration());
		bulletTrail = new EntityPrototype()
				.animation(explosionAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletTrailLight)
				.timeToLive(explosionAnimation.getAnimationDuration())
				.with(Traits.fadeOut(1f))
				.zSpeed(10);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new FireSlime(origin, this);
	}

	Entity createBullet(Vector2 origin) {
		return new Projectile(origin, bullet) {
			@Override
			protected void onExpire() {
				GameState.addEntity(createBulletExplosion(getPos()));
			}
		};
	}

	private Entity createBulletTrail(Entity entity) {
		return new Entity(entity.getPos(), bulletTrail);
	}

	private Entity createBulletExplosion(Vector2 origin) {
		return new Entity(origin, bulletExplosion);
	}

}
