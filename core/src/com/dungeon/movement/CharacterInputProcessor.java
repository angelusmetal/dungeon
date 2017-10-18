package com.dungeon.movement;

import com.badlogic.gdx.InputProcessor;
import com.dungeon.character.Character;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CharacterInputProcessor implements InputProcessor {

	private static class KeyMapping {
		Consumer<Integer> onKeyDown;
		Consumer<Integer> onKeyUp;
		KeyMapping(Consumer<Integer> onKeyDown, Consumer<Integer> onKeyUp) {
			this.onKeyDown = onKeyDown;
			this.onKeyUp = onKeyUp;
		}
	}

	private final Map<Integer, KeyMapping> buttonControllers = new HashMap<>();

	public void addPovMovementController(int up, int down, int left, int right, Character character) {
		buttonControllers.put(up, new KeyMapping(i -> character.setSelfYMovement(character.getSelfMovement().y + 1), i -> character.setSelfYMovement(character.getSelfMovement().y - 1)));
		buttonControllers.put(down, new KeyMapping(i -> character.setSelfYMovement(character.getSelfMovement().y - 1), i -> character.setSelfYMovement(character.getSelfMovement().y + 1)));
		buttonControllers.put(left, new KeyMapping(i -> character.setSelfXMovement(character.getSelfMovement().x - 1), i -> character.setSelfXMovement(character.getSelfMovement().x + 1)));
		buttonControllers.put(right, new KeyMapping(i -> character.setSelfXMovement(character.getSelfMovement().x + 1), i -> character.setSelfXMovement(character.getSelfMovement().x - 1)));
	}

	public void addPovAimController(int up, int down, int left, int right, Character character) {
		buttonControllers.put(up, new KeyMapping(i -> character.setAimY(character.getAim().y + 1), i -> character.setAimY(character.getAim().y - 1)));
		buttonControllers.put(down, new KeyMapping(i -> character.setAimY(character.getAim().y - 1), i -> character.setAimY(character.getAim().y + 1)));
		buttonControllers.put(left, new KeyMapping(i -> character.setAimX(character.getAim().x - 1), i -> character.setAimX(character.getAim().x + 1)));
		buttonControllers.put(right, new KeyMapping(i -> character.setAimX(character.getAim().x + 1), i -> character.setAimX(character.getAim().x - 1)));
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
