package com.dungeon.engine.entity;

// TODO Do we still need this interface?
public interface TraitSupplier<T> {
    Trait<T> get(T t);
}
