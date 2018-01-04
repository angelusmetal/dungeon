package com.dungeon.engine.controller;

import com.badlogic.gdx.math.Vector2;

@FunctionalInterface
public interface StickListener {
	void onUpdate(Vector2 vector);
}
