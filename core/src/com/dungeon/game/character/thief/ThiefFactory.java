package com.dungeon.game.character.thief;

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
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class ThiefFactory implements EntityFactory.EntityTypeFactory {

	public static final String THIEF_WALK = "thief_walk";
	public static final String THIEF_ATTACK = "thief_attack";
	public static final String THIEF_IDLE = "thief_idle";
	public static final String PROJECTILE_FLY = "projectile_thief_fly";
	public static final String PROJECTILE_EXPLODE = "projectile_thief_explode";

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlyAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final EntityPrototype character;
	private final EntityPrototype bullet;
	private final EntityPrototype bulletExplosion;
	private final EntityPrototype bulletTrail;

	final Light bulletLight;
	final Light bulletTrailLight;

	public ThiefFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("THIEF");
		int health = config.getLong("health", 60L).intValue();
		float speed = config.getLong("speed", 96L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();
		float bulletSpeed = config.getLong("bulletSpeed", 200L).floatValue();
		float bulletDamage = config.getLong("bulletDamage", 25L).floatValue();

		idleAnimation = ResourceManager.getAnimation(THIEF_IDLE);
		walkAnimation = ResourceManager.getAnimation(THIEF_WALK);
		attackAnimation = ResourceManager.getAnimation(THIEF_ATTACK);
		bulletFlyAnimation = ResourceManager.getAnimation(PROJECTILE_FLY);
		bulletExplodeAnimation = ResourceManager.getAnimation(PROJECTILE_EXPLODE);

		Vector2 characterBoundingBox = new Vector2(14, 12);
		Vector2 characterDrawOffset = new Vector2(16, 6);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(4, 4);

		bulletLight = new Light(60, new Color(0.3f, 0.9f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		bulletTrailLight = new Light(20, new Color(0.3f, 0.9f, 0.2f, 0.1f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.health(health)
				.speed(speed)
				.friction(friction);
		bullet = new EntityPrototype()
				.animation(bulletFlyAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(bulletSpeed)
				.timeToLive(3)
				.bounciness(1)
				.hitPredicate(PlayerCharacter.HIT_NON_PLAYERS)
				.damage(bulletDamage)
				.with(Traits.generator(0.052f, this::createBulletTrail));
		bulletExplosion = new EntityPrototype()
				.animation(bulletExplodeAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		bulletTrail = new EntityPrototype()
				.animation(bulletExplodeAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletTrailLight)
				.timeToLive(bulletExplodeAnimation.getAnimationDuration())
				.with(Traits.fadeOut(0.3f))
				.with(Traits.fadeOutLight())
				.zSpeed(0);
	}

	public Thief build(Vector2 origin) {
		return new Thief(origin, this);
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
