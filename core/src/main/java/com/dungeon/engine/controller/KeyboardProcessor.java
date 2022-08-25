package com.dungeon.engine.controller;

import com.badlogic.gdx.InputProcessor;
import com.dungeon.engine.controller.toggle.Toggle;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows registering toggles for different keys
 */
public class KeyboardProcessor implements InputProcessor {
	private final Map<Integer, Toggle> toggles = new HashMap<>();

	/**
	 * Regoster a toggle with a keycode
	 * @return true if there was already a mapping (which gets overwritten)
	 */
	public boolean register(int keycode, Toggle toggle) {
		return toggles.put(keycode, toggle) != null;
	}

	@Override
	public boolean keyDown(int keycode) {
		Toggle toggle = toggles.get(keycode);
		if (toggle != null) {
			toggle.notifyListeners(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		Toggle toggle = toggles.get(keycode);
		if (toggle != null) {
			toggle.notifyListeners(false);
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
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
