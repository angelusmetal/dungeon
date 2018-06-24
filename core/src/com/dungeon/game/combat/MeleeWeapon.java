package com.dungeon.game.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.render.DrawFunction;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.state.GameState;

import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class MeleeWeapon extends Weapon {

	public static final String HIT_ANIMATION = "invisible";

	private final EntityPrototype hit;
	private final EntityPrototype slash;

	public MeleeWeapon(String name, Supplier<Float> damage, DamageType damageType, float knockback) {
		super(name, damage, damageType, knockback);
		Animation<TextureRegion> hitAnimation = ResourceManager.getAnimation(HIT_ANIMATION);
		Animation<TextureRegion> slashAnimation = getAttackAnimation();
		Vector2 hitBoundingBox = getHitBox();
		TextureRegion referenceFrame = slashAnimation.getKeyFrame(0);
		Vector2 hitDrawOffset = new Vector2(referenceFrame.getRegionWidth() / 2, referenceFrame.getRegionHeight() / 2);

		hit = new EntityPrototype()
				.animation(hitAnimation)
				.boundingBox(hitBoundingBox)
				.drawOffset(hitDrawOffset)
				.speed(0)
				.timeToLive(slashAnimation.getAnimationDuration())
				.hitPredicate(getHitPredicate());

		slash = new EntityPrototype()
				.animation(slashAnimation)
				.boundingBox(hitBoundingBox)
				.drawOffset(hitDrawOffset)
				.timeToLive(slashAnimation.getAnimationDuration());
	}

	protected abstract Animation<TextureRegion> getAttackAnimation();
	protected abstract Vector2 getHitBox();
	protected abstract Predicate<Entity> getHitPredicate();
	protected abstract float getSpawnDistance();

	@Override
	public void spawnEntities(Vector2 origin, Vector2 aim) {
		Vector2 hitOrigin = shift(origin, aim, getSpawnDistance());
		GameState.addEntity(createSlash(hitOrigin, aim));
		GameState.addEntity(createHit(hitOrigin));
	}

	private Entity createHit(Vector2 origin) {
		return new Projectile(origin, hit, this::createAttack) {
			@Override protected void onTileCollision(boolean horizontal) {}
		};
	}

	private Entity createSlash(Vector2 origin, Vector2 aim) {
		Entity entity = new Entity(origin, slash);
		entity.setDrawFunction(DrawFunction.rotateVector(aim).get());
		return entity;
	}

}
