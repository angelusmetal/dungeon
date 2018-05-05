package com.dungeon.engine.entity;

public interface TraitSupplier<T> {
    Trait<T> get(T t);
}
