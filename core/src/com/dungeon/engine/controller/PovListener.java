package com.dungeon.engine.controller;

import com.badlogic.gdx.controllers.PovDirection;

@FunctionalInterface
public interface PovListener {
	void onUpdate(PovDirection direction);
}
