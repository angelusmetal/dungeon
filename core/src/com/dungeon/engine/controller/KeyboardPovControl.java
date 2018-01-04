package com.dungeon.engine.controller;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.PovDirection;

/**
 * A POV control backed by a keyboard
 */
class KeyboardPovControl extends PovControl implements InputProcessor {

	private static final PovDirection[] POV_DIRECTIONS = {
			PovDirection.northWest,
			PovDirection.north,
			PovDirection.northEast,
			PovDirection.west,
			PovDirection.center,
			PovDirection.east,
			PovDirection.southWest,
			PovDirection.south,
			PovDirection.southEast};

	private final int up;
	private final int down;
	private final int left;
	private final int right;

	private int povIndex = 4;

	public KeyboardPovControl(int up, int down, int left, int right) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == up) {
			povIndex -= 3;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (keycode == down) {
			povIndex += 3;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (keycode == left) {
			povIndex -= 1;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (keycode == right) {
			povIndex += 1;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == up) {
			povIndex += 3;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (keycode == down) {
			povIndex -= 3;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (keycode == left) {
			povIndex += 1;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else if (keycode == right) {
			povIndex -= 1;
			updateListeners(POV_DIRECTIONS[povIndex]);
		} else {
			return false;
		}
		return true;
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
