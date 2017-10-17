package com.dungeon.movement;

import com.badlogic.gdx.InputProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MovableInputProcessor implements InputProcessor {

	private static class KeyMapping {
		Consumer<Integer> onKeyDown;
		Consumer<Integer> onKeyUp;
		KeyMapping(Consumer<Integer> onKeyDown, Consumer<Integer> onKeyUp) {
			this.onKeyDown = onKeyDown;
			this.onKeyUp = onKeyUp;
		}
	}

	private final Map<Integer, KeyMapping> buttonControllers = new HashMap<>();

	public void addPovController(int up, int down, int left, int right, Movable movable) {
		buttonControllers.put(up, new KeyMapping((i) -> movable.setSelfYMovement(1), (i) -> movable.setSelfYMovement(0)));
		buttonControllers.put(down, new KeyMapping((i) -> movable.setSelfYMovement(-1), (i) -> movable.setSelfYMovement(0)));
		buttonControllers.put(left, new KeyMapping((i) -> movable.setSelfXMovement(-1), (i) -> movable.setSelfXMovement(0)));
		buttonControllers.put(right, new KeyMapping((i) -> movable.setSelfXMovement(1), (i) -> movable.setSelfXMovement(0)));
	}

	public void addButtonController(int buttonCode, Consumer<Integer> action) {
		buttonControllers.put(buttonCode, new KeyMapping(action, (i) -> {}));
	}

	public void clear() {
		buttonControllers.clear();
	}

	@Override
	public boolean keyDown(int keycode) {
		System.out.println("down " + keycode);
		KeyMapping keyMapping = buttonControllers.get(keycode);
		if (keyMapping != null) {
			keyMapping.onKeyDown.accept(keycode);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		System.out.println("up " + keycode);
		KeyMapping keyMapping = buttonControllers.get(keycode);
		if (keyMapping != null) {
			keyMapping.onKeyUp.accept(keycode);
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
