package com.dungeon.engine.controller.directional;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;

/**
 * A directional control backed by a keyboard
 */
public class KeyboardDirectionalControl extends DirectionalControl implements InputProcessor {

	private static final PovDirection[] POV_DIRECTIONS = {
			PovDirection.southWest,
			PovDirection.south,
			PovDirection.southEast,
			PovDirection.west,
			PovDirection.center,
			PovDirection.east,
			PovDirection.northWest,
			PovDirection.north,
			PovDirection.northEast
	};

	private static final Vector2[] VECTOR_DIRECTIONS = {
			new Vector2(-1,-1),
			new Vector2(0,-1),
			new Vector2(1,-1),
			new Vector2(-1,0),
			new Vector2(0,0),
			new Vector2(1,0),
			new Vector2(-1,1),
			new Vector2(0,1),
			new Vector2(1,1)};

	private final int up;
	private final int down;
	private final int left;
	private final int right;

	private int povIndex = 4;

	/**
	 * Create a directional control governed by 4 directional keyboard keys
	 * @param up Key for moving up
	 * @param down Key for moving down
	 * @param left Key for moving left
	 * @param right Key for moving right
	 */
	public KeyboardDirectionalControl(int up, int down, int left, int right) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == up) {
			povIndex += 3;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else if (keycode == down) {
			povIndex -= 3;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else if (keycode == left) {
			povIndex -= 1;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else if (keycode == right) {
			povIndex += 1;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == up) {
			povIndex -= 3;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else if (keycode == down) {
			povIndex += 3;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else if (keycode == left) {
			povIndex += 1;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
		} else if (keycode == right) {
			povIndex -= 1;
			updateListeners(POV_DIRECTIONS[povIndex], VECTOR_DIRECTIONS[povIndex]);
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
