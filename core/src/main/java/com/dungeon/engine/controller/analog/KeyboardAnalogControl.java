package com.dungeon.engine.controller.analog;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * A analog control backed by a keyboard
 */
public class KeyboardAnalogControl extends AnalogControl implements InputProcessor {

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

	private boolean upPressed;
	private boolean downPressed;
	private boolean leftPressed;
	private boolean rightPressed;

	/**
	 * Create a analog control governed by 4 analog keyboard keys
	 * @param up Key for moving up
	 * @param down Key for moving down
	 * @param left Key for moving left
	 * @param right Key for moving right
	 */
	public KeyboardAnalogControl(int up, int down, int left, int right) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == up) {
			upPressed = true;
			notifyListeners();
		} else if (keycode == down) {
			downPressed = true;
			notifyListeners();
		} else if (keycode == left) {
			leftPressed = true;
			notifyListeners();
		} else if (keycode == right) {
			rightPressed = true;
			notifyListeners();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == up) {
			upPressed = false;
			notifyListeners();
		} else if (keycode == down) {
			downPressed = false;
			notifyListeners();
		} else if (keycode == left) {
			leftPressed = false;
			notifyListeners();
		} else if (keycode == right) {
			rightPressed = false;
			notifyListeners();
		} else {
			return false;
		}
		return true;
	}

	private void notifyListeners() {
		int povIndex = 4
				+ (leftPressed ? -1 : 0)
				+ (rightPressed ? 1 : 0)
				+ (upPressed ? 3 : 0)
				+ (downPressed ? -3 : 0);
		notifyListeners(VECTOR_DIRECTIONS[povIndex]);
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
