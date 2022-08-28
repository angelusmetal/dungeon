package com.marines.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dungeon.engine.render.light.LightRenderer2;
import com.dungeon.engine.render.light.RenderLight;
import com.dungeon.engine.resource.Resources;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MarinesLauncher extends ApplicationAdapter implements InputProcessor, ControllerListener {

    public static final float CAMERA_SPEED = 5f;
    public static final float CONTROLLER_DEADZONE = 0.2f;

    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private LightRenderer2 lightRenderer;

    private BitmapFont font;
    private Texture floor;
    private Texture normalMap;
    private FrameBuffer normalMapBuffer;
    private List<RenderLight> lights;
    private final Matrix4 ortho = new Matrix4();
    private final Vector3 mouseCursor = new Vector3();
    private final FloatArray geometry = new FloatArray();
    private boolean blendLight = true;
    private boolean addSpecular = true;

    public MarinesLauncher() {
    }

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1800;
        config.height = 900;
        config.resizable = false;
        new LwjglApplication(new MarinesLauncher(), config);
    }

    @Override
    public void create () {
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        ortho.setToOrtho2D(0, 0, camera.viewportWidth, camera.viewportHeight);
        viewport = new ExtendViewport(1800, 900, camera);
        viewport.update(1800, 900);
        font = new BitmapFont();
        floor = new Texture("gfx/cobblestone_floor_a.png");
        normalMap = new Texture("gfx/normal_map/cobblestone_floor_a.png");
        normalMapBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        lightRenderer = new LightRenderer2();
        lightRenderer.create(camera, normalMapBuffer);
        lightRenderer.setUseNormalMapping(true);
        Controllers.addListener(this);
        Gdx.input.setInputProcessor(this);
        lights = new ArrayList<>();
        lights.add(new RenderLight(new Vector3(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 100f), 400, 10, Color.ORANGE, true));
        lights.add(new RenderLight(new Vector3(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 100f), 200, 10, new Color(.2f, .2f, .4f, 1f), true));

        rectangle(geometry, 800f, 1000f, 650f, 850f);
        circle(geometry, new Vector2(200f, 700f), 100f, 20);
        circle(geometry, new Vector2(1400f, 300f), 50f, 20);
        circle(geometry, new Vector2(1300f, 800f), 50f, 3);
        for (int i = -50; i < 50; ++i) {
            for (int j = -50; j < 50; ++j) {
                circle(geometry, new Vector2(i * 100, j * 100f), 10f, 6);
            }
        }
    }

    @Override
    public void render() {
        Controller current = Controllers.getCurrent();
        float translateX = controllerNorm(current.getAxis(current.getMapping().axisLeftX)) * camera.zoom * CAMERA_SPEED;
        float translateY = controllerNorm(current.getAxis(current.getMapping().axisLeftY)) * camera.zoom * CAMERA_SPEED;
        float zoom = controllerNorm(current.getAxis(current.getMapping().axisRightY));
        float lightHeight = controllerNorm(current.getAxis(current.getMapping().axisRightX));
        lights.get(0).getOrigin().z += lightHeight;
        camera.zoom *= 1 + zoom * 0.01;
        camera.translate(translateX, -translateY, 0);
        camera.update();
        // Update light
        lights.get(1).getOrigin().set(mouseCursor);
        camera.unproject(lights.get(1).getOrigin());

        float left = camera.position.x - camera.viewportWidth / 2 * camera.zoom;
        float right = camera.position.x + camera.viewportWidth / 2 * camera.zoom;
        float down = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
        float up = camera.position.y + camera.viewportHeight / 2 * camera.zoom;

        int leftTile = (int) left / normalMap.getWidth() - 1;
        int rightTile = (int) right / normalMap.getWidth() + 1;
        int downTile = (int) down / normalMap.getHeight() - 1;
        int upTile = (int) up / normalMap.getHeight() + 1;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.begin();
        for (int x = leftTile; x < rightTile; ++x) {
            for (int y = downTile; y < upTile; ++y) {
                spriteBatch.draw(floor, x * floor.getWidth(), y * floor.getHeight());
            }
        }
        spriteBatch.end();

        viewport.apply();
        normalMapBuffer.begin();
        ScreenUtils.clear(0f, 0f, 0f, 0f);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        for (int x = leftTile; x < rightTile; ++x) {
            for (int y = downTile; y < upTile; ++y) {
                spriteBatch.draw(normalMap, x * normalMap.getWidth(), y * normalMap.getHeight());
            }
        }
        spriteBatch.end();
        normalMapBuffer.end();

        lightRenderer.render(lights, geometry);

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(ortho);
        if (blendLight) {
            spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
            lightRenderer.drawToScreen(spriteBatch);
        }
        if (addSpecular) {
            spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
            lightRenderer.drawToScreen(spriteBatch);
        }
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        font.draw(spriteBatch, "Position: " + camera.position + ", width: " + camera.viewportWidth + ", height: " + camera.viewportHeight + ", zoom: " + camera.zoom, 0, 890);
        font.draw(spriteBatch, "Left: " + left + ", right: " + right + ", down: " + down + ", up: " + up, 0, 870);
        font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, 850);
        spriteBatch.end();
    }


   	@Override
	public void resize(int width, int height) {
        viewport.update(width, height);
        // TODO recreate normalMapBuffer with the new size
	}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        normalMapBuffer.dispose();
        Resources.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F1) {
            blendLight = !blendLight;
        } else if (keycode == Input.Keys.F2) {
            addSpecular = !addSpecular;
        }
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
        mouseCursor.set(screenX, screenY, 0f);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    private float controllerNorm(float value) {
        if (abs(value) > CONTROLLER_DEADZONE) {
            return value;
        } else {
            return 0f;
        }
    }

    public static void rectangle(FloatArray geometry, float left, float right, float bottom, float top) {
        geometry.add(left);
        geometry.add(bottom);
        geometry.add(right);
        geometry.add(bottom);

        geometry.add(right);
        geometry.add(bottom);
        geometry.add(right);
        geometry.add(top);

        geometry.add(right);
        geometry.add(top);
        geometry.add(left);
        geometry.add(top);

        geometry.add(left);
        geometry.add(top);
        geometry.add(left);
        geometry.add(bottom);
    }

    public static void circle(FloatArray geometry, Vector2 origin, float radius, int segments) {
        Vector2 step = new Vector2(0, radius);
        Vector2 vertex = origin.cpy().add(step);
        for (int i = 0; i < segments; ++i) {
            geometry.add(vertex.x);
            geometry.add(vertex.y);
            step.rotate(360f / segments);
            vertex.set(origin).add(step);
            geometry.add(vertex.x);
            geometry.add(vertex.y);
        }
    }
}