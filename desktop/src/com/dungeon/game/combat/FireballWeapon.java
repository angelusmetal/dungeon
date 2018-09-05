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

public class FireballWeapon extends ProjectileWeapon {

	private static final String PROJECTILE = "slime_fire_projectile";
	private static final String EXPLOSION = "slime_fire_explosion";

	private final EntityPrototype projectile;
	private final EntityPrototype impact;
	private final EntityPrototype trail;

	public FireballWeapon() {
		super("Fireball", damageSupplier(), DamageType.ELEMENTAL, 0);

		Animation<TextureRegion> projectileAnimation = Resources.animations.get(PROJECTILE);
		Animation<TextureRegion> explosionAnimation = Resources.animations.get(EXPLOSION);

		Vector2 bulletBoundingBox = new Vector2(6, 6);
		Vector2 bulletDrawOffset = new Vector2(8, 8);

		float bulletSpeed = 100f;
		Light bulletLight = new Light(50, new Color(1, 0.5f, 0, 0.5f), Lights.NORMAL, Light.torchlight());
		Light bulletTrailLight = new Light(20, new Color(1, 0.5f, 0, 0.5f), Lights.NORMAL, Light.torchlight());

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
				Engine.entities.add(createImpact(getOrigin()));
			}
		};
	}

	private Entity createTrail(Entity generator) {
		return new Entity(trail, generator.getOrigin());
	}

	private Entity createImpact(Vector2 origin) {
		return new Entity(impact, origin);
	}

	private static Supplier<Float> damageSupplier() {
		float tier = Game.getDifficultyTier();
		return () -> tier * Rand.between(2f, 5f);
	}
}