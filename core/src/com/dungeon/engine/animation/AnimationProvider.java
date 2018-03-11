package com.dungeon.engine.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;
import java.util.function.Function;

public class AnimationProvider<T extends Enum<T>> {
	private final EnumMap<T, Function<Float, GameAnimation<T>>> factory;

	public AnimationProvider(Class<T> keyType) {
		this.factory = new EnumMap<>(keyType);
	}

	public AnimationProvider register(T type, Animation<TextureRegion> animation) {
		factory.put(type, (time) -> new com.dungeon.engine.animation.GameAnimation<>(type, animation, time));
		return this;
	}

	public AnimationProvider register(T type, Animation<TextureRegion> animation, Runnable runnable) {
		factory.put(type, (time) -> new com.dungeon.engine.animation.GameAnimation<>(type, animation, time, runnable));
		return this;
	}

	public com.dungeon.engine.animation.GameAnimation<T> get(T type, float time) {
		return factory.get(type).apply(time);
	}

}
