package com.dungeon.engine.render.effect;

public interface RenderEffect {
	void render();
	boolean isExpired();
	void dispose();
}
