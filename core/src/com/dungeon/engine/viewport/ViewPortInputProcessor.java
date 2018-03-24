package com.dungeon.engine.viewport;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class ViewPortInputProcessor implements GestureDetector.GestureListener, InputProcessor {

	public static final float[] SCALES = {0.125f, 0.25f, 0.5f, 1f, 2f, 3f, 4f, 5f, 6f};

	private final ViewPort viewPort;
	private boolean zoomEnabled = true;
	private int scaleIndex = 7;

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
		if (zoomEnabled) {
			// Change scale based on mouse wheel
			if (amount < 0) {
				scaleIndex = Math.min(scaleIndex - amount, SCALES.length - 1);
				viewPort.scale = SCALES[scaleIndex];
			}
			else if (amount > 0) {
				scaleIndex = Math.max(scaleIndex - amount, 0);
				viewPort.scale = SCALES[scaleIndex];
			}
		}
		return zoomEnabled;
	}
}
