package com.dungeon.engine.controller;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;

@FunctionalInterface
public interface DirectionalListener {
	void onUpdate(PovDirection direction, Vector2 vector);
}
