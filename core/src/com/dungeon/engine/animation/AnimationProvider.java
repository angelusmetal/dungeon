package com.dungeon.engine.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.game.state.GameState;

import java.util.EnumMap;
import java.util.function.Supplier;

public class AnimationProvider<T extends Enum<T>> {
	private final EnumMap<T, Supplier<GameAnimation<T>>> factory;
	private final GameState state;

	public AnimationProvider(Class<T> keyType, GameState state) {
		this.factory = new EnumMap<>(keyType);
		this.state = state;
	}

	public AnimationProvider register(T type, Animation<TextureRegion> animation) {
		factory.put(type, () -> new com.dungeon.engine.animation.GameAnimation<>(type, animation, state.getStateTime()));
		return this;
	}

	public AnimationProvider register(T type, Animation<TextureRegion> animation, Runnable runnable) {
		factory.put(type, () -> new com.dungeon.engine.animation.GameAnimation<>(type, animation, state.getStateTime(), runnable));
		return this;
	}

	public com.dungeon.engine.animation.GameAnimation<T> get(T type) {
		return factory.get(type).get();
	}

}
