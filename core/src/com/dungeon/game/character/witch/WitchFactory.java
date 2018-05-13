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
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.object.tombstone.TombstoneFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.CatProjectileSheet;
import com.dungeon.game.tileset.CharactersSheet32;
import com.dungeon.game.tileset.ProjectileSheet;

import java.util.function.Function;

public class WitchFactory implements EntityFactory.EntityTypeFactory {

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

	final Function<Vector2, Entity> tombstoneSpawner;

	public WitchFactory(TombstoneFactory tombstoneFactory) {
		idleAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.WITCH_IDLE, CharactersSheet32::witchIdle);
		walkAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.WITCH_WALK, CharactersSheet32::witchWalk);
		attackAnimation = ResourceManager.instance().getAnimation(CharactersSheet32.WITCH_ATTACK, CharactersSheet32::witchAttack);
		bulletFlySideAnimation = ResourceManager.instance().getAnimation(CatProjectileSheet.FLY_RIGHT, CatProjectileSheet::flyRight);
		bulletFlyNorthAnimation = ResourceManager.instance().getAnimation(CatProjectileSheet.FLY_UP, CatProjectileSheet::flyUp);
		bulletFlySouthAnimation = ResourceManager.instance().getAnimation(CatProjectileSheet.FLY_DOWN, CatProjectileSheet::flyDown);
		bulletExplodeAnimation = ResourceManager.instance().getAnimation(ProjectileSheet.WITCH_EXPLODE, ProjectileSheet::witchExplode);

		Vector2 characterBoundingBox = new Vector2(14, 22);
		Vector2 characterDrawOffset = new Vector2(16, 13);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(12, 10);

		Light bulletLight = new Light(60, new Color(0.8f, 0.2f, 0.8f, 0.5f), Light.FLARE_TEXTURE, () -> 1f, Light::noRotate);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.friction(10f);
		bullet = new EntityPrototype()
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(200)
				.timeToLive(10)
				.targetPredicate(PlayerCharacter.IS_NON_PLAYER)
				.damage(25)
				.with(Traits.autoSeek(0.1f, 60, () -> GameState.getEntities().stream().filter(PlayerCharacter.IS_NON_PLAYER)))
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

		tombstoneSpawner = tombstoneFactory::build;
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
