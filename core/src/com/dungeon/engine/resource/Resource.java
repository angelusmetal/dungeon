package com.dungeon.engine.resource;

public interface Resource<T> {
	void load();
	void unload();
	T get();
}
