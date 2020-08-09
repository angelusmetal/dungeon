package com.dungeon.engine.controller.analog;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.toggle.Toggle;

/**
 * A analog control backed by a keyboard
 */
public class KeyboardAnalogControl extends AnalogControl {

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
	public KeyboardAnalogControl(Toggle up, Toggle down, Toggle left, Toggle right) {
		up.addListener(this::up);
		down.addListener(this::down);
		left.addListener(this::left);
		right.addListener(this::right);
	}

	private void up(boolean pressed) {
		upPressed = pressed;
		notifyListeners();
	}

	private void down(boolean pressed) {
		downPressed = pressed;
		notifyListeners();
	}

	private void left(boolean pressed) {
		leftPressed = pressed;
		notifyListeners();
	}

	private void right(boolean pressed) {
		rightPressed = pressed;
		notifyListeners();
	}

	private void notifyListeners() {
		int povIndex = 4
				+ (leftPressed ? -1 : 0)
				+ (rightPressed ? 1 : 0)
				+ (upPressed ? 3 : 0)
				+ (downPressed ? -3 : 0);
		notifyListeners(VECTOR_DIRECTIONS[povIndex]);
	}

}
