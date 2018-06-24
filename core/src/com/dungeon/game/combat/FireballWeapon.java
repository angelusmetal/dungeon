package com.dungeon.game.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.state.GameState;

public class FireballWeapon extends ProjectileWeapon {

	private static final String PROJECTILE = "slime_fire_projectile";
	private static final String EXPLOSION = "slime_fire_explosion";

	private final EntityPrototype projectile;
	private final EntityPrototype impact;
	private final EntityPrototype trail;

	public FireballWeapon() {
		super("Fireball", () -> 5f * (GameState.getPlayerCount() + GameState.getLevelCount()), DamageType.ELEMENTAL, 0);

		Animation<TextureRegion> projectileAnimation = ResourceManager.getAnimation(PROJECTILE);
		Animation<TextureRegion> explosionAnimation = ResourceManager.getAnimation(EXPLOSION);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(8, 8);

		float bulletSpeed = 100f;
		Light bulletLight = new Light(50, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		Light bulletTrailLight = new Light(20, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

		projectile = new EntityPrototype()
				.animation(projectileAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(bulletSpeed)
				.timeToLive(10)
				.hitPredicate(PlayerEntity.HIT_PLAYERS)
				.with(Traits.generator(0.1f, this::createTrail));
		impact = new EntityPrototype()
				.animation(explosionAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(explosionAnimation.getAnimationDuration());
		trail = new EntityPrototype()
				.animation(explosionAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletTrailLight)
				.timeToLive(explosionAnimation.getAnimationDuration())
				.with(Traits.fadeOut(1f))
				.zSpeed(10);	}

	@Override
	protected float getSpawnDistance() {
		return 2;
	}

	@Override
	protected Entity createProjectile(Vector2 origin, Vector2 aim) {
		return new Projectile(origin, projectile, this::createAttack) {
			@Override
			protected void onExpire() {
				GameState.addEntity(createImpact(getPos()));
			}
		};
	}

	private Entity createTrail(Entity generator) {
		return new Entity(generator.getPos(), trail);
	}

	private Entity createImpact(Vector2 origin) {
		return new Entity(origin, impact);
	}
}
