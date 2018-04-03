package com.dungeon.engine.render.effect;

import com.dungeon.game.state.GameState;

public interface RenderEffect {
	void render(GameState state);
	boolean isExpired(float time);
	void dispose();
}
