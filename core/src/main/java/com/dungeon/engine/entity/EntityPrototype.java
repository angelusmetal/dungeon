package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.LightPrototype;
import com.dungeon.engine.render.ShadowType;
import com.dungeon.engine.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.dungeon.engine.util.Util.clamp;

public class EntityPrototype {
	List<Vector2> occlusionSegments;
	Supplier<Animation<TextureRegion>> animation = () -> null;
	boolean offsetAnimation;
	float bounciness = 0;
	Supplier<Color> color = Color.WHITE::cpy;
	Supplier<Float> knockback = () -> 0f;
	Supplier<Float> friction = () -> 0f;
	Supplier<Float> airFriction = () -> 0f;
	LightPrototype light = null;
	LightPrototype flare = null;
	List<TraitSupplier<Entity>> traits = new ArrayList<>();
	List<TraitSupplier<Entity>> onHitTraits = new ArrayList<>();
	List<TraitSupplier<Entity>> onExpireTraits = new ArrayList<>();
	List<TraitSupplier<Entity>> onGroundHitTraits = new ArrayList<>();
	List<TraitSupplier<Entity>> onRestTraits = new ArrayList<>();
	List<TraitSupplier<Entity>> onSignalTraits = new ArrayList<>();
	Supplier<Float> speed = () -> 1f;
	Supplier<Float> zSpeed = () -> 0f;
	int zIndex = 0;
	float z = 0;
	Predicate<Entity> hitPredicate = entity -> false;
	Supplier<Float> timeToLive = () -> null;
	Supplier<Integer> health = () -> 100;
	boolean canBlock = false;
	boolean canBeBlockedByEntities = false;
	boolean canBeBlockedByTiles = true;
	boolean canBeHit = false;
	boolean canBeHurt = false;
	boolean isStatic = false;
	ShadowType shadowType = ShadowType.NONE;

	Vector2 boundingBox = Vector2.Zero;
	Vector2 drawOffset = Vector2.Zero;
	Vector2 boundingBoxOffset = Vector2.Zero;
	private String factory;

	public EntityPrototype() {}

	public EntityPrototype(EntityPrototype other) {
		this.animation = other.animation;
		this.offsetAnimation = other.offsetAnimation;
		this.bounciness = other.bounciness;
		this.color = other.color;
		this.knockback = other.knockback;
		this.friction = other.friction;
		this.airFriction = other.airFriction;
		this.light = other.light;
		this.flare = other.flare;
		this.traits = new ArrayList<>(other.traits);
		this.onHitTraits = new ArrayList<>(other.onHitTraits);
		this.onExpireTraits = new ArrayList<>(other.onExpireTraits);
		this.onGroundHitTraits = new ArrayList<>(other.onGroundHitTraits);
		this.onRestTraits = new ArrayList<>(other.onRestTraits);
		this.onSignalTraits = new ArrayList<>(other.onSignalTraits);
		this.speed = other.speed;
		this.zSpeed = other.zSpeed;
		this.zIndex = other.zIndex;
		this.z = other.z;
		this.hitPredicate = other.hitPredicate;
		this.timeToLive = other.timeToLive;
		this.health = other.health;
		this.boundingBox = other.boundingBox.cpy();
		this.boundingBoxOffset = other.boundingBoxOffset.cpy();
		this.drawOffset = other.drawOffset.cpy();
		this.canBlock = other.canBlock;
		this.canBeBlockedByEntities = other.canBeBlockedByEntities;
		this.canBeBlockedByTiles = other.canBeBlockedByTiles;
		this.canBeHit = other.canBeHit;
		this.canBeHurt = other.canBeHurt;
		this.shadowType = other.shadowType;
		this.factory = other.factory;
		this.isStatic = other.isStatic;
		this.occlusionSegments = other.occlusionSegments;
	}

	public EntityPrototype animation(Animation<TextureRegion> animation) {
		this.animation = () -> animation;
		return this;
	}

	public EntityPrototype animation(Supplier<Animation<TextureRegion>> animation) {
		this.animation = animation;
		return this;
	}

	public Supplier<Animation<TextureRegion>> getAnimation() {
		return animation;
	}

	public EntityPrototype offsetAnimation(boolean offsetAnimation) {
		this.offsetAnimation = offsetAnimation;
		return this;
	}

	public EntityPrototype bounciness(float bounciness) {
		this.bounciness = clamp(bounciness);
		return this;
	}

	public EntityPrototype boundingBox(Vector2 boundingBox) {
		this.boundingBox = boundingBox;
		return this;
	}

	public EntityPrototype boundingBoxOffset(Vector2 boundingBoxOffset) {
		this.boundingBoxOffset = boundingBoxOffset;
		return this;
	}

	public EntityPrototype occlusionSegments(List<Vector2> vertexes) {
		this.occlusionSegments = vertexes;
		return this;
	}

	/**
	 * @return A list of occlusion segments derived from the bounding box
	 * (used for when there are no custom segments defined)
	 */
	public ArrayList<Vector2> occlusionSegmentsFromBoundingBox() {
		ArrayList<Vector2> vertexes = new ArrayList<>(16);
		if (boundingBoxOffset.len2() > 0) {
			// If there is a defined offset
			vertexes.add(new Vector2(-boundingBoxOffset.x, -boundingBoxOffset.y));
			vertexes.add(new Vector2(boundingBox.x - boundingBoxOffset.x, -boundingBoxOffset.y));

			vertexes.add(new Vector2(boundingBox.x - boundingBoxOffset.x, -boundingBoxOffset.y));
			vertexes.add(new Vector2(boundingBox.x - boundingBoxOffset.x, boundingBox.y - boundingBoxOffset.y));

			vertexes.add(new Vector2(boundingBox.x - boundingBoxOffset.x, boundingBox.y - boundingBoxOffset.y));
			vertexes.add(new Vector2(-boundingBoxOffset.x, boundingBox.y - boundingBoxOffset.y));

			vertexes.add(new Vector2(-boundingBoxOffset.x, boundingBox.y - boundingBoxOffset.y));
			vertexes.add(new Vector2(-boundingBoxOffset.x, -boundingBoxOffset.y));
		} else {
			// Otherwise, centered
			vertexes.add(new Vector2(-boundingBox.x / 2f, -boundingBox.y / 2f));
			vertexes.add(new Vector2(boundingBox.x / 2f, -boundingBox.y / 2f));

			vertexes.add(new Vector2(boundingBox.x / 2f, -boundingBox.y / 2f));
			vertexes.add(new Vector2(boundingBox.x / 2f, boundingBox.y / 2f));

			vertexes.add(new Vector2(boundingBox.x / 2f, boundingBox.y / 2f));
			vertexes.add(new Vector2(-boundingBox.x / 2f, boundingBox.y / 2f));

			vertexes.add(new Vector2(-boundingBox.x / 2f, boundingBox.y / 2f));
			vertexes.add(new Vector2(-boundingBox.x / 2f, -boundingBox.y / 2f));
		}
		return vertexes;
	}

	public EntityPrototype shadowType(ShadowType shadowType) {
		this.shadowType = shadowType;
		return this;
	}

	public EntityPrototype color(Color color) {
		this.color = color::cpy;
		return this;
	}

	public EntityPrototype color(Supplier<Color> color) {
		this.color = color;
		return this;
	}

	public EntityPrototype health(int health) {
		this.health = () -> health;
		return this;
	}

	public EntityPrototype health(Supplier<Integer> health) {
		this.health = health;
		return this;
	}

	public EntityPrototype drawOffset(Vector2 drawOffset) {
		this.drawOffset = drawOffset;
		return this;
	}

	public EntityPrototype knockback(float knockback) {
		this.knockback = () -> knockback;
		return this;
	}

	public EntityPrototype knockback(Supplier<Float> knockback) {
		this.knockback = knockback;
		return this;
	}

	public EntityPrototype friction(float friction) {
		final float f = clamp(friction);
		this.friction = () -> f;
		return this;
	}

	public EntityPrototype friction(Supplier<Float> friction) {
		this.friction = friction;
		return this;
	}

	public EntityPrototype airFriction(float airFriction) {
		final float f = clamp(airFriction);
		this.airFriction = () -> f;
		return this;
	}

	public EntityPrototype airFriction(Supplier<Float> airFriction) {
		this.airFriction = airFriction;
		return this;
	}

	public EntityPrototype hitPredicate(Predicate<Entity> hitPredicate) {
		this.hitPredicate = hitPredicate;
		return this;
	}

	public EntityPrototype light(LightPrototype light) {
		this.light = light;
		return this;
	}

	public EntityPrototype flare(LightPrototype flare) {
		this.flare = flare;
		return this;
	}

	public EntityPrototype with(TraitSupplier<Entity> trait) {
		this.traits.add(trait);
		return this;
	}

	public EntityPrototype speed(float speed) {
		this.speed = () -> speed;
		return this;
	}

	public EntityPrototype speed(Supplier<Float> speed) {
		this.speed = speed;
		return this;
	}

	public EntityPrototype timeToLive(float timeToLive) {
		this.timeToLive = () -> timeToLive;
		return this;
	}

	public EntityPrototype timeToLive(Supplier<Float> timeToLive) {
		this.timeToLive = timeToLive;
		return this;
	}

	public EntityPrototype zIndex(int zIndex) {
		this.zIndex = zIndex;
		return this;
	}

	public EntityPrototype zSpeed(float zSpeed) {
		this.zSpeed = () -> zSpeed;
		return this;
	}

	public EntityPrototype z(float z) {
		this.z = z;
		return this;
	}

	public EntityPrototype zSpeed(Supplier<Float> zSpeed) {
		this.zSpeed = zSpeed;
		return this;
	}

	public EntityPrototype canBlock(boolean canBlock) {
		this.canBlock = canBlock;
		return this;
	}

	public EntityPrototype canBeBlockedByEntities(boolean canBeBlockedByEntities) {
		this.canBeBlockedByEntities = canBeBlockedByEntities;
		return this;
	}

	public EntityPrototype canBeBlockedByTiles(boolean canBeBlockedByTiles) {
		this.canBeBlockedByTiles = canBeBlockedByTiles;
		return this;
	}

	public EntityPrototype canBeHit(boolean canBeHit) {
		this.canBeHit = canBeHit;
		return this;
	}

	public EntityPrototype canBeHurt(boolean canBeHurt) {
		this.canBeHurt = canBeHurt;
		return this;
	}

	public void isStatic(Boolean isStatic) {
		this.isStatic = isStatic;
	}

	public EntityPrototype onHit(TraitSupplier<Entity> trait) {
		this.onHitTraits.add(trait);
		return this;
	}

	public EntityPrototype onExpire(TraitSupplier<Entity> trait) {
		this.onExpireTraits.add(trait);
		return this;
	}

	public EntityPrototype onGroundHit(TraitSupplier<Entity> trait) {
		this.onGroundHitTraits.add(trait);
		return this;
	}

	public EntityPrototype onRest(TraitSupplier<Entity> trait) {
		this.onRestTraits.add(trait);
		return this;
	}

	public EntityPrototype onSignal(TraitSupplier<Entity> trait) {
		this.onSignalTraits.add(trait);
		return this;
	}

	public EntityPrototype factory(String factory) {
		this.factory = factory;
		return this;
	}

	public String getFactory() {
		return factory;
	}

}

