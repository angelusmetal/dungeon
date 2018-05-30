package com.dungeon.game.character.witch;

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

public class WitchFactory implements EntityFactory.EntityTypeFactory {

	public static final String WITCH_WALK = "witch_walk";
	public static final String WITCH_ATTACK = "witch_attack";
	public static final String WITCH_IDLE = "witch_idle";

	public static final String PROJECTILE_RIGHT = "projectile_cat_right";
	public static final String PROJECTILE_UP = "projectile_cat_up";
	public static final String PROJECTILE_DOWN = "projectile_cat_down";
	//public static final String WITCH_FLY = "projectile_witch_fly";
	public static final String PROJECTILE_EXPLODE = "projectile_witch_explode";

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlySideAnimation;
	final Animation<TextureRegion> bulletFlyNorthAnimation;
	final Animation<TextureRegion> bulletFlySouthAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final EntityPrototype character;
	private final EntityPrototype bullet;
	private final EntityPrototype bulletExplosion;
	private final EntityPrototype bulletTrail;

	public WitchFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("WITCH");
		int health = config.getLong("health", 90L).intValue();
		float speed = config.getLong("speed", 60L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();
		float bulletSpeed = config.getLong("bulletSpeed", 200L).floatValue();
		float bulletDamage = config.getLong("bulletDamage", 25L).floatValue();

		idleAnimation = ResourceManager.getAnimation(WITCH_IDLE);
		walkAnimation = ResourceManager.getAnimation(WITCH_WALK);
		attackAnimation = ResourceManager.getAnimation(WITCH_ATTACK);
		bulletFlySideAnimation = ResourceManager.getAnimation(PROJECTILE_RIGHT);
		bulletFlyNorthAnimation = ResourceManager.getAnimation(PROJECTILE_UP);
		bulletFlySouthAnimation = ResourceManager.getAnimation(PROJECTILE_DOWN);
		bulletExplodeAnimation = ResourceManager.getAnimation(PROJECTILE_EXPLODE);

		Vector2 characterBoundingBox = new Vector2(14, 12);
		Vector2 characterDrawOffset = new Vector2(16, 6);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(12, 10);

		Light bulletLight = new Light(60, new Color(0.8f, 0.2f, 0.8f, 0.5f), Light.FLARE_TEXTURE, () -> 1f, Light::noRotate);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.health(health)
				.speed(speed)
				.friction(friction);
		bullet = new EntityPrototype()
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(bulletSpeed)
				.timeToLive(10)
				.hitPredicate(PlayerCharacter.HIT_NON_PLAYERS)
				.damage(bulletDamage)
				.with(Traits.autoSeek(0.1f, 60, () -> GameState.getEntities().stream().filter(PlayerCharacter.TARGET_NON_PLAYER_CHARACTERS)))
				.with(Traits.animationByVector(Entity::getMovement, bulletFlySideAnimation, bulletFlyNorthAnimation, bulletFlySouthAnimation))
				.with(Traits.generator(0.05f, this::createBulletTrail));
		bulletExplosion = new EntityPrototype()
				.animation(bulletExplodeAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		bulletTrail = new EntityPrototype()
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(0.4f)
				.with(Traits.fadeOut(0.7f))
				.with(Traits.fadeOutLight())
				.with(Traits.zAccel(100f));
	}

	public Witch build(Vector2 origin) {
		return new Witch(origin, this);
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
		// TODO Is this hack ok?
		Entity trail = new Entity(entity.getPos(), bulletTrail);
		trail.setCurrentAnimation(entity.getCurrentAnimation());
		return trail;
	}

	private Entity createBulletExplosion(Vector2 origin) {
		return new Entity(origin, bulletExplosion);
	}
}
