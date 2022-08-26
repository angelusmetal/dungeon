package com.dungeon.desktop.shader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ControllerTest extends ApplicationAdapter implements InputProcessor {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1800;
		config.height = 900;
		config.resizable = false;
		new LwjglApplication(new ControllerTest(), config);
	}


	public static final float CURSOR_RADIUS = 30;
	public static final float MOVEMENT_RADIUS = 200;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private Camera camera;
	private BitmapFont font;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		font = new BitmapFont();
	}

	@Override
	public void render() {
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapeRenderer.setProjectionMatrix(camera.combined);
		for (Controller controller : Controllers.getControllers()) {
			// Axes
			float leftX = controller.getAxis(controller.getMapping().axisLeftX);
			float rightX = controller.getAxis(controller.getMapping().axisRightX);
			float leftY = controller.getAxis(controller.getMapping().axisLeftY);
			float rightY = controller.getAxis(controller.getMapping().axisRightY);
			// Buttons
			boolean buttonA = controller.getButton(controller.getMapping().buttonA);
			boolean buttonB = controller.getButton(controller.getMapping().buttonB);
			boolean buttonX = controller.getButton(controller.getMapping().buttonX);
			boolean buttonY = controller.getButton(controller.getMapping().buttonY);
			boolean buttonDown = controller.getButton(controller.getMapping().buttonDpadDown);
			boolean buttonLeft = controller.getButton(controller.getMapping().buttonDpadLeft);
			boolean buttonRight = controller.getButton(controller.getMapping().buttonDpadRight);
			boolean buttonUp = controller.getButton(controller.getMapping().buttonDpadUp);
			boolean buttonStart = controller.getButton(controller.getMapping().buttonStart);
			boolean buttonBack = controller.getButton(controller.getMapping().buttonBack);
			// Shoulder
			boolean buttonL1 = controller.getButton(controller.getMapping().buttonL1);
			float buttonL2 = controller.getAxis(4);
			boolean buttonR1 = controller.getButton(controller.getMapping().buttonR1);
			float buttonR2 = controller.getAxis(5);
			// Display labels
			spriteBatch.setProjectionMatrix(camera.combined);
			spriteBatch.begin();
			spriteBatch.setColor(Color.WHITE);
			font.draw(spriteBatch, "x: " + leftX + ", y: " + leftY, -890, 440);
			font.draw(spriteBatch, "x: " + rightX + ", y: " + rightY, 10, 440);
			spriteBatch.end();

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			// Display thumbsticks
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.circle(-450 + leftX * MOVEMENT_RADIUS, 0 + leftY * -MOVEMENT_RADIUS, CURSOR_RADIUS);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.circle(450 + rightX * MOVEMENT_RADIUS, 0 + rightY * -MOVEMENT_RADIUS, CURSOR_RADIUS);

			// Display buttons
			if (buttonA) {
				shapeRenderer.setColor(Color.GREEN);
				shapeRenderer.circle(450, -400, CURSOR_RADIUS);
			}
			if (buttonB) {
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.circle(500, -350, CURSOR_RADIUS);
			}
			if (buttonX) {
				shapeRenderer.setColor(Color.BLUE);
				shapeRenderer.circle(400, -350, CURSOR_RADIUS);
			}
			if (buttonY) {
				shapeRenderer.setColor(Color.YELLOW);
				shapeRenderer.circle(450, -300, CURSOR_RADIUS);
			}
			if (buttonDown) {
				shapeRenderer.setColor(Color.YELLOW);
				shapeRenderer.circle(-450, -400, CURSOR_RADIUS);
			}
			if (buttonRight) {
				shapeRenderer.setColor(Color.YELLOW);
				shapeRenderer.circle(-400, -350, CURSOR_RADIUS);
			}
			if (buttonLeft) {
				shapeRenderer.setColor(Color.YELLOW);
				shapeRenderer.circle(-500, -350, CURSOR_RADIUS);
			}
			if (buttonUp) {
				shapeRenderer.setColor(Color.YELLOW);
				shapeRenderer.circle(-450, -300, CURSOR_RADIUS);
			}

			// Display shoulders
			if (buttonL1) {
				shapeRenderer.setColor(Color.GREEN);
				shapeRenderer.rect(-600, 350, 300, 30);
			}
			shapeRenderer.setColor(0f, 0f, buttonL2, 0f);
			shapeRenderer.rect(-450 - buttonL2 * 150, 400, buttonL2 * 300, 30);
			if (buttonR1) {
				shapeRenderer.setColor(Color.GREEN);
				shapeRenderer.rect(300, 350, 300, 30);
			}
			shapeRenderer.setColor(0f, 0f, buttonR2, 0f);
			shapeRenderer.rect(450 - buttonR2 * 150, 400, buttonR2 * 300, 30);

			if (buttonStart) {
				shapeRenderer.setColor(Color.CYAN);
				shapeRenderer.rect(300, 300, 300, 30);
			}
			if (buttonBack) {
				shapeRenderer.setColor(Color.CYAN);
				shapeRenderer.rect(-600, 300, 300, 30);
			}
			shapeRenderer.end();
			break;
		}

	}


//	@Override
//	public void resize(int width, int height) {
//	}
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		spriteBatch.dispose();
	}

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
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return true;
	}

}