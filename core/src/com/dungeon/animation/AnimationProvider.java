package com.dungeon.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.GameState;

import java.util.EnumMap;
import java.util.function.Supplier;

public class AnimationProvider<T extends Enum<T>> {
	private final EnumMap<T, Supplier<GameAnimation<T>>> factory;
	private final GameState state;

	public AnimationProvider(Class<T> keyType, GameState state) {
		this.factory = new EnumMap<>(keyType);
		this.state = state;
	}

	public void register(T type, Animation<TextureRegion> animation) {
		factory.put(type, () -> new GameAnimation<>(type, animation, state.getStateTime()));
	}

	public void register(T type, Animation<TextureRegion> animation, Runnable runnable) {
		factory.put(type, () -> new GameAnimation<>(type, animation, state.getStateTime(), runnable));
	}

	public GameAnimation<T> get(T type) {
		return factory.get(type).get();
	}
}
