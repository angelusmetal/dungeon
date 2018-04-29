package com.dungeon.engine.entity;

public interface MutatorSupplier<T> {
    Mutator<T> get(T t);
}
