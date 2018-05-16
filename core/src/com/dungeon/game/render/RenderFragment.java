package com.dungeon.game.render;

import com.badlogic.gdx.utils.Disposable;

public interface RenderFragment extends Disposable {
	void render();
	void toggle();
	// TODO Add getter for render calls
}
