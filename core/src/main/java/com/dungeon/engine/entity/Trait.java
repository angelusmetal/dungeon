package com.dungeon.engine.entity;

import java.util.function.Consumer;

@FunctionalInterface
public interface Trait<T> extends Consumer<T> {
	default boolean isExpired() {
		return false;
	}
	default void onExpire() {}
}
