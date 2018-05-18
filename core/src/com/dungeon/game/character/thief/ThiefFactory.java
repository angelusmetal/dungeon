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
import com.dungeon.game.object.tombstone.TombstoneFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CharactersSheet32;
import com.dungeon.game.tileset.ProjectileSheet;
import com.moandjiezana.toml.Toml;

import java.util.function.Function;

public class ThiefFactory implements EntityFactory.EntityTypeFactory {

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

	final Function<Vector2, Entity> tombstoneSpawner;

	public ThiefFactory(TombstoneFactory tombstoneFactory) {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("THIEF");
		int health = config.getLong("health", 60L).intValue();
		float speed = config.getLong("speed", 96L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();
		float bulletSpeed = config.getLong("bulletSpeed", 200L).floatValue();
		float bulletDamage = config.getLong("bulletDamage", 25L).floatValue();

		idleAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.THIEF_IDLE, CharactersSheet32::thiefIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.THIEF_WALK, CharactersSheet32::thiefWalk);
		attackAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.THIEF_ATTACK, CharactersSheet32::thiefAttack);
		bulletFlyAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.THIEF_FLY, ProjectileSheet::thiefFly);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.THIEF_EXPLODE, ProjectileSheet::thiefExplode);

		Vector2 characterBoundingBox = new Vector2(14, 21);
		Vector2 characterDrawOffset = new Vector2(16, 12);

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
				.targetPredicate(PlayerCharacter.IS_NON_PLAYER)
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

		tombstoneSpawner = tombstoneFactory::build;
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
