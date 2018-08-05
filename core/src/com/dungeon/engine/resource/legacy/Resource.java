package com.dungeon.engine.resource.legacy;

public interface Resource<T> {
	void load();
	void unload();
	T get();
}
