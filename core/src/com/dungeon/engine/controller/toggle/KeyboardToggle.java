package com.dungeon.engine.controller.toggle;

import com.badlogic.gdx.InputProcessor;

public class KeyboardToggle extends Toggle implements InputProcessor {

	private final int keycode;

	public KeyboardToggle(int keycode) {
		this.keycode = keycode;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == this.keycode) {
			notifyListeners(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == this.keycode) {
			notifyListeners(false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
