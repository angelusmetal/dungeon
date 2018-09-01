package com.dungeon.game.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.game.Game;
import com.dungeon.game.resource.Lights;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.entity.Projectile;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;
import com.dungeon.engine.Engine;

import java.util.function.Supplier;

public class CatStaffWeapon extends ProjectileWeapon {

	private static final String PROJECTILE_RIGHT = "projectile_cat_right";
	private static final String PROJECTILE_UP = "projectile_cat_up";
	private static final String PROJECTILE_DOWN = "projectile_cat_down";
	private static final String PROJECTILE_EXPLODE = "projectile_witch_explode";

	private final EntityPrototype projectile;
	private final EntityPrototype impact;
	private final EntityPrototype trail;

	public CatStaffWeapon() {
		super("Cat staff", damageSupplier(), DamageType.ELEMENTAL, 0);

		Animation<TextureRegion> bulletFlySideAnimation = Resources.animations.get(PROJECTILE_RIGHT);
		Animation<TextureRegion> bulletFlyNorthAnimation = Resources.animations.get(PROJECTILE_UP);
		Animation<TextureRegion> bulletFlySouthAnimation = Resources.animations.get(PROJECTILE_DOWN);
		Animation<TextureRegion> bulletExplodeAnimation = Resources.animations.get(PROJECTILE_EXPLODE);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(12, 10);

		float bulletSpeed = 200f;
		Light bulletLight = new Light(60, new Color(0.8f, 0.2f, 0.8f, 0.5f), Lights.FLARE);

		projectile = new EntityPrototype()
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.speed(bulletSpeed)
				.timeToLive(10)
				.hitPredicate(PlayerEntity.HIT_NON_PLAYERS)
				// TODO Should this use the radius filter instead?
				.with(Traits.autoSeek(0.1f, 60, () -> Engine.entities.all().filter(PlayerEntity.TARGET_NON_PLAYER_CHARACTERS)))
				.with(Traits.animationByVector(Entity::getMovement, bulletFlySideAnimation, bulletFlyNorthAnimation, bulletFlySouthAnimation))
				.with(Traits.generator(0.05f, this::createTrail));
		impact = new EntityPrototype()
				.animation(bulletExplodeAnimation)
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(bulletExplodeAnimation.getAnimationDuration());
		trail = new EntityPrototype()
				.boundingBox(bulletBoundingBox)
				.drawOffset(bulletDrawOffset)
				.light(bulletLight)
				.timeToLive(0.4f)
				.with(Traits.fadeOut(0.7f))
				.with(Traits.fadeOutLight())
				.with(Traits.zAccel(100f));
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
				Engine.entities.add(createImpact(getOrigin()));
			}
		};
	}

	private Entity createTrail(Entity generator) {
		Entity entity = new Entity(trail, generator.getOrigin());
		entity.setCurrentAnimation(generator.getCurrentAnimation());
		entity.setInvertX(generator.invertX());
		return entity;
	}

	private Entity createImpact(Vector2 origin) {
		return new Entity(impact, origin);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 3f);
	}
}
