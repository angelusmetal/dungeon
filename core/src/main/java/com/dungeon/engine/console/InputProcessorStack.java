package com.dungeon.engine.console;

import com.badlogic.gdx.InputProcessor;

import java.util.Stack;

public class InputProcessorStack implements InputProcessor {
	Stack<InputProcessor> stack = new Stack<>();

	public void push(InputProcessor processor) {
		stack.push(processor);
	}

	public InputProcessor pop() {
		return stack.pop();
	}

	@Override
	public boolean keyDown(int keycode) {
		return stack.peek().keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return stack.peek().keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return stack.peek().keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return stack.peek().touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return stack.peek().touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return stack.peek().touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return stack.peek().mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
		return stack.peek().scrolled(amount);
	}
}