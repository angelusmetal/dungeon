package com.dungeon.engine;

import com.badlogic.gdx.ApplicationListener;

import java.util.LinkedList;

public class ApplicationListenerStack implements ApplicationListener {
	LinkedList<ApplicationListener> stack = new LinkedList<>();

	public void push(ApplicationListener listener) {
		stack.push(listener);
	}

	public ApplicationListener pop() {
		return stack.pop();
	}

	@Override
	public void create () {}

	@Override
	public void resize (int width, int height) {
		if (!stack.isEmpty()) {
			stack.peek().resize(width, height);
		}
	}

	@Override
	public void render () {
		if (!stack.isEmpty()) {
			stack.peek().render();
		}
	}

	@Override
	public void pause () {
		if (!stack.isEmpty()) {
			stack.peek().pause();
		}
	}

	@Override
	public void resume () {
		if (!stack.isEmpty()) {
			stack.peek().resume();
		}
	}

	/** Pops all frames and disposes each one */
	public void dispose () {
		while(!stack.isEmpty()) {
			stack.pop().dispose();
		}
	}

}
