package com.dungeon.game.character.assassin;

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

public class AssassinFactory implements EntityFactory.EntityTypeFactory {

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> bulletFlyAnimation;
	final Animation<TextureRegion> bulletExplodeAnimation;

	final EntityPrototype character;
	private final EntityPrototype bullet;
	private final EntityPrototype bulletExplosion;
	private final EntityPrototype bulletTrail;

	final Function<Vector2, Entity> tombstoneSpawner;

	public AssassinFactory(TombstoneFactory tombstoneFactory) {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("ASSASSIN");
		int health = config.getLong("health", 100L).intValue();
		float speed = config.getLong("speed", 60L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();
		float bulletSpeed = config.getLong("bulletSpeed", 180L).floatValue();
		float bulletDamage = config.getLong("bulletDamage", 35L).floatValue();

		attackAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.ASSASSIN_ATTACK, CharactersSheet32::assassinAttack);
		idleAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.ASSASSIN_IDLE, CharactersSheet32::assassinIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.ASSASSIN_WALK, CharactersSheet32::assassinWalk);
		bulletFlyAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.ASSASSIN_FLY, ProjectileSheet::assasinFly);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.ASSASSIN_EXPLODE, ProjectileSheet::assasinExplode);

		Vector2 characterBoundingBox = new Vector2(14, 22);
		Vector2 characterDrawOffset = new Vector2(16, 13);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(8, 8);

		Light bulletLight = new Light(60, new Color(0.8f, 0.3f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light bulletTrailLight = new Light(60, new Color(0.8f, 0.3f, 0.2f, 0.2f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

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
				.timeToLive(10)
				.hitPredicate(PlayerCharacter.HIT_NON_PLAYERS)
				.damage(bulletDamage)
				.with(Traits.generator(0.1f, this::createBulletTrail));
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
				.zSpeed(10);

		tombstoneSpawner = tombstoneFactory::build;
	}


	public Assassin build(Vector2 origin) {
		return new Assassin(origin, this);
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
