package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.DrawFunction;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityPrototype {
	Supplier<Animation<TextureRegion>> animation = () -> null;
	float bounciness = 0;
	Supplier<Color> color = Color.WHITE::cpy;
	Supplier<Float> knockback = () -> 0f;
	Supplier<Float> friction = () -> 0f;
	Light light = null;
	List<TraitSupplier<Entity>> traits = new ArrayList<>();
	List<TraitSupplier<Entity>> onHitTraits = new ArrayList<>();
	List<TraitSupplier<Entity>> onExpireTraits = new ArrayList<>();
	List<TraitSupplier<Entity>> onRestTraits = new ArrayList<>();
	Supplier<Float> speed = () -> 1f;
	Supplier<Float> zSpeed = () -> 0f;
	int zIndex = 0;
	float z = 0;
	Predicate<Entity> hitPredicate = (entity) -> false;
	Supplier<Float> timeToLive = () -> null;
	Supplier<DrawFunction> drawFunction = DrawFunction.regular();
	Supplier<Integer> health = () -> 100;
	boolean solid = false;
	boolean canBeHit = false;
	boolean canBeHurt = false;
	boolean castsShadow = false;

	Vector2 boundingBox = Vector2.Zero;
	Vector2 drawOffset = Vector2.Zero;
	Vector2 boundingBoxOffset = Vector2.Zero;

	public EntityPrototype() {}

	public EntityPrototype(EntityPrototype other) {
		this.animation = other.animation;
		this.bounciness = other.bounciness;
		this.color = other.color;
		this.knockback = other.knockback;
		this.friction = other.friction;
		this.light = other.light;
		this.traits = new ArrayList<>(other.traits);
		this.onHitTraits = new ArrayList<>(other.onHitTraits);
		this.onExpireTraits = new ArrayList<>(other.onExpireTraits);
		this.onRestTraits = new ArrayList<>(other.onRestTraits);
		this.speed = other.speed;
		this.zSpeed = other.zSpeed;
		this.zIndex = other.zIndex;
		this.z = other.z;
		this.hitPredicate = other.hitPredicate;
		this.timeToLive = other.timeToLive;
		this.drawFunction = other.drawFunction;
		this.health = other.health;
		this.boundingBox = other.boundingBox.cpy();
		this.boundingBoxOffset = other.boundingBoxOffset.cpy();
		this.drawOffset = other.drawOffset.cpy();
		this.solid = other.solid;
		this.canBeHit = other.canBeHit;
		this.canBeHurt = other.canBeHurt;
		this.castsShadow = other.castsShadow;
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

	public EntityPrototype bounciness(float bounciness) {
		this.bounciness = Util.clamp(bounciness);
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

	public EntityPrototype castsShadow(boolean castsShadow) {
		this.castsShadow = castsShadow;
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

	public EntityPrototype drawFunction(Supplier<DrawFunction> drawFunction) {
		this.drawFunction = drawFunction;
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
		this.friction = () -> friction;
		return this;
	}

	public EntityPrototype friction(Supplier<Float> friction) {
		this.friction = friction;
		return this;
	}

	public EntityPrototype hitPredicate(Predicate<Entity> hitPredicate) {
		this.hitPredicate = hitPredicate;
		return this;
	}

	public EntityPrototype light(Light light) {
		this.light = light;
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

	public EntityPrototype solid(boolean solid) {
		this.solid = solid;
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

	public EntityPrototype onHit(TraitSupplier<Entity> trait) {
		this.onHitTraits.add(trait);
		return this;
	}

	public EntityPrototype onExpire(TraitSupplier<Entity> trait) {
		this.onExpireTraits.add(trait);
		return this;
	}

	public EntityPrototype onRest(TraitSupplier<Entity> trait) {
		this.onRestTraits.add(trait);
		return this;
	}

}
