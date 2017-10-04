package com.dungeon;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class ViewPortInputProcessor implements GestureDetector.GestureListener, InputProcessor {

	public static final float MIN_SCALE = 1;
	public static final float MAX_SCALE = 6;

	private final ViewPort viewPort;
	private boolean zoomEnabled = true;

	public ViewPortInputProcessor(ViewPort viewPort) {
		this.viewPort = viewPort;
	}

	public void setZoomEnabled(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}

	/// Gesture methods
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		viewPort.xOffset += deltaX / viewPort.scale;
		viewPort.yOffset -= deltaY / viewPort.scale;
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		System.out.println("pan stop!");
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		System.out.println("zoom!");
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		System.out.println("pinch!");
		return false;
	}

	@Override
	public void pinchStop() {
		System.out.println("pinch stop!");
	}

	/// InputProcessor methods

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
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
		// Change scale based on mouse wheel
		if (amount > 0 && viewPort.scale > MIN_SCALE) {
			viewPort.scale -= amount;
		}
		else if (amount < 0 && viewPort.scale < MAX_SCALE) {
			viewPort.scale -= amount;
		}
		return true;
	}
}
