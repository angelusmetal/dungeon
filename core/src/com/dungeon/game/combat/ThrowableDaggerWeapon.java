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

public class ThrowableDaggerWeapon extends ProjectileWeapon {

	private static final String PROJECTILE_FLY = "projectile_thief_fly";
	private static final String PROJECTILE_EXPLODE = "projectile_thief_explode";

	private final EntityPrototype projectile;
	private final EntityPrototype impact;
	private final EntityPrototype trail;

	public ThrowableDaggerWeapon() {
		super(() -> 35f, DamageType.ELEMENTAL, 0);

		Animation<TextureRegion> bulletFlyAnimation = ResourceManager.getAnimation(PROJECTILE_FLY);
		Animation<TextureRegion> bulletExplodeAnimation = ResourceManager.getAnimation(PROJECTILE_EXPLODE);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(4, 4);

		float bulletSpeed = 200f;
		Light bulletLight = new Light(60, new Color(0.3f, 0.9f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		Light bulletTrailLight = new Light(20, new Color(0.3f, 0.9f, 0.2f, 0.1f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		projectile = new EntityPrototype()
				.animation(bulletFlyAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(bulletSpeed)
				.timeToLive(3)
				.bounciness(1)
				.hitPredicate(PlayerEntity.HIT_NON_PLAYERS)
				.with(Traits.generator(0.052f, this::createTrail));
		impact = new EntityPrototype()
				.animation(bulletExplodeAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		trail = new EntityPrototype()
				.animation(bulletExplodeAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletTrailLight)
				.timeToLive(bulletExplodeAnimation.getAnimationDuration())
				.with(Traits.fadeOut(0.3f))
				.with(Traits.fadeOutLight())
				.zSpeed(0);
	}

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
		Entity entity = new Entity(generator.getPos(), trail);
		entity.setCurrentAnimation(generator.getCurrentAnimation());
		return entity;
	}

	private Entity createImpact(Vector2 origin) {
		return new Entity(origin, impact);
	}
}
