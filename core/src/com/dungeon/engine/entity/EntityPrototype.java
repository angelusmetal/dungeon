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
	Animation<TextureRegion> animation;
	float bounciness = 0;
	Supplier<Color> color = Color.WHITE::cpy;
	Supplier<Float> damage;
	Supplier<Float> friction = () -> 0f;
	Light light = null;
	List<TraitSupplier<Entity>> traits = new ArrayList<>();
	Supplier<Float> speed = () -> 1f;
	Supplier<Float> zSpeed = () -> 0f;
	int zIndex = 0;
	Predicate<Entity> targetPredicate = (entity) -> false;
	Supplier<Float> timeToLive = () -> null;
	Supplier<DrawFunction> drawFunction = DrawFunction.regular();
	Supplier<Integer> health = () -> 100;

	Vector2 boundingBox = Vector2.Zero;
	Vector2 drawOffset = Vector2.Zero;

	public EntityPrototype animation(Animation<TextureRegion> animation) {
		this.animation = animation;
		return this;
	}

	public EntityPrototype bounciness(float bounciness) {
		this.bounciness = Util.clamp(bounciness);
		return this;
	}

	public EntityPrototype boundingBox(Vector2 boundingBox) {
		this.boundingBox = boundingBox;
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

	public EntityPrototype damage(float damage) {
		this.damage = () -> damage;
		return this;
	}

	public EntityPrototype damage(Supplier<Float> damage) {
		this.damage = damage;
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

	public EntityPrototype friction(float friction) {
		this.friction = () -> friction;
		return this;
	}

	public EntityPrototype friction(Supplier<Float> friction) {
		this.friction = friction;
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

	public EntityPrototype targetPredicate(Predicate<Entity> targetPredicate) {
		this.targetPredicate = targetPredicate;
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

	public EntityPrototype zSpeed(Supplier<Float> zSpeed) {
		this.zSpeed = zSpeed;
		return this;
	}

}

