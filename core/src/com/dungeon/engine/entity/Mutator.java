package com.dungeon.engine.entity;

import com.dungeon.game.state.GameState;

import java.util.function.BiConsumer;

public interface Mutator<T> extends BiConsumer<T, GameState> {
}
