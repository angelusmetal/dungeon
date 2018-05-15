package com.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.NoiseBuffer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.Console;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ViewPortRenderer implements Disposable {

	private final ViewPort viewPort;

	private SpriteBatch batch;

	private ViewPortBuffer lightingBuffer;
	private ViewPortBuffer viewportBuffer;
	private NoiseBuffer noiseBuffer;
	private BlendFunctionContext lightingContext;
	private BlendFunctionContext noiseContext;
	private BitmapFont font;

	// Developer tools
	private boolean renderScene = true;
	private boolean renderLighting = true;
	private boolean renderHealthbars = false;
	private boolean renderBoundingBoxes = false;
	private boolean renderNoise = true;

	private final Comparator<? super Entity> comp = (e1, e2) ->
			e1.getZIndex() > e2.getZIndex() ? 1 :
			e1.getZIndex() < e2.getZIndex() ? -1 :
			e1.getPos().y > e2.getPos().y ? -1 :
			e1.getPos().y < e2.getPos().y ? 1 :
			e1.getPos().x < e2.getPos().x ? -1 :
			e1.getPos().x > e2.getPos().x ? 1 : 0;

	private final Predicate<? super Entity> entityInCamera;
	private final Predicate<? super Entity> lightInCamera;

	private final Color baseLight = Color.WHITE.cpy();
	private final TextureRegion fill;
	private final Toml configuration;
	private final float gamma;

	private static final ColorContext BOUNDING_BOXES = new ColorContext(new Color(1f, 0.2f, 0.2f, 0.3f));

	private int renderCalls = 0;
	private float frameTime;

	public ViewPortRenderer(ViewPort viewPort, Toml configuration) {
		this.viewPort = viewPort;
		this.configuration = configuration;
		this.gamma = configuration.getDouble("viewport.gamma", 1.0d).floatValue();
		this.lightingBuffer = new ViewPortBuffer(viewPort);
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.noiseBuffer = new NoiseBuffer(GameState.getConfiguration());
		this.lightingContext = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.noiseContext = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		entityInCamera = (e) ->
			e.getPos().x - e.getDrawOffset().x < viewPort.cameraX + viewPort.cameraWidth &&
			e.getPos().x + e.getDrawOffset().x + e.getFrame().getRegionWidth() > viewPort.cameraX &&
			e.getPos().y - e.getDrawOffset().y + e.getZPos() < viewPort.cameraY + viewPort.cameraHeight &&
			e.getPos().y + e.getDrawOffset().y + e.getZPos() + e.getFrame().getRegionHeight() > viewPort.cameraY;
		lightInCamera = (e) ->
			e.getLight() != null &&
			e.getPos().x - e.getLight().diameter < viewPort.cameraX + viewPort.cameraWidth &&
			e.getPos().x + e.getLight().diameter > viewPort.cameraX &&
			e.getPos().y - e.getLight().diameter < viewPort.cameraY + viewPort.cameraHeight &&
			e.getPos().y + e.getLight().diameter > viewPort.cameraY;

		fill = new TextureRegion(ResourceManager.instance().getTexture("fill.png"));
		this.font = new BitmapFont();
	}

	public void initialize() {
		batch = new SpriteBatch();

		resetBuffers();

		randomizeBaseLight();
	}

	private void resetBuffers() {
		lightingBuffer.reset();
		viewportBuffer.reset();
		noiseBuffer.renderNoise();
	}

	public void randomizeBaseLight() {
		baseLight.set(Util.hsvaToColor(Rand.between(0f, 1f), 0.3f, 1f, 1f));
	}

	public void render () {
		int currentRenderCalls = 0;
		long start = System.nanoTime();

		// Render light in a separate frame buffer
		if (renderLighting) {
			renderLight();
		}

		// Render scene
		if (renderScene) {
			viewportBuffer.render((batch) -> {
				// Draw map
				drawMap(batch);
				// Iterate entities in render order and draw them
				GameState.getEntities().stream().filter(entityInCamera).sorted(comp).forEach(e -> e.draw(batch, viewPort));
			});
		} else {
			viewportBuffer.render((batch) -> {
				Gdx.gl.glClearColor(1, 1, 1, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			});
		}

		// Draw lighting on top of scene
		if (renderLighting) {
			viewportBuffer.render((batch) -> {
				batch.setColor(baseLight);
				lightingContext.run(batch, lightingBuffer::draw);
				batch.setColor(Color.WHITE);
			});
		}

		// Render UI elements
		if (renderHealthbars) {
			viewportBuffer.render((batch) -> {
				GameState.getEntities().stream().filter(e -> e instanceof Character).filter(entityInCamera).map(e -> (Character)e).sorted(comp).forEach(e -> e.drawHealthbar(batch, viewPort));
			});
		}

		if (renderBoundingBoxes) {
			viewportBuffer.render((batch) -> {
				BOUNDING_BOXES.set(batch);
				GameState.getEntities().stream().filter(entityInCamera).forEach(e -> viewPort.draw(
						batch,
						fill,
						e.getBody().getBottomLeft().x,
						e.getBody().getBottomLeft().y,
						e.getBody().getBoundingBox().x,
						e.getBody().getBoundingBox().y));
				BOUNDING_BOXES.unset(batch);
			});
		}

		if (renderNoise) {
			viewportBuffer.render((batch) -> {
				noiseContext.run(batch, (b) -> noiseBuffer.draw(batch, viewPort.cameraWidth, viewPort.cameraHeight));
			});
		}

		batch.begin();
		viewportBuffer.drawScaled(batch);
		currentRenderCalls += batch.renderCalls;
		batch.end();

		currentRenderCalls += lightingBuffer.getLastRenderCalls();
		lightingBuffer.resetLastRenderCalls();
		currentRenderCalls += viewportBuffer.getLastRenderCalls();
		viewportBuffer.resetLastRenderCalls();
		renderCalls = currentRenderCalls;
		frameTime = (System.nanoTime() - start) / 1_000_000f;

		// TODO This should actually go elsewhere (separated from viewport)
		drawConsole();
	}

	private void drawConsole() {
		batch.begin();
		int x = 10;
		int y = viewPort.height - 10;
		for (Console.LogLine log : GameState.console().getLog()) {
			log.color.a = Math.max((log.expiration - GameState.time()) / GameState.console().getMessageExpiration(), 0);
			font.setColor(log.color);
			font.draw(batch, log.message, x, y);
			y -= 16;
		}

		font.setColor(Color.WHITE);

		x = viewPort.width - 200;
		y = viewPort.height - 10;
		for (Map.Entry<String, Supplier<String>> watch : GameState.console().getWatches().entrySet()) {
			font.draw(batch, watch.getKey() + ": " + watch.getValue().get(), x, y);
			y -= 16;
		}
		batch.end();
	}

	private void renderLight() {
		lightingBuffer.render((batch) -> {
			// remember SpriteBatch's current functions
			int srcFunc = batch.getBlendSrcFunc();
			int dstFunc = batch.getBlendDstFunc();
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);

//			Gdx.gl.glClearColor(baseLight.r, baseLight.g, baseLight.b, baseLight.a);
			Gdx.gl.glClearColor(gamma, gamma, gamma, baseLight.a);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			GameState.getEntities().stream().filter(lightInCamera).forEach(e -> e.drawLight(batch, viewPort));

			// Restore blend function
			batch.setBlendFunction(srcFunc, dstFunc);
		});
	}

	private void drawMap(SpriteBatch batch) {
		// Only render the visible portion of the map
		int tSize = GameState.getLevelTileset().tile_size;
		int minX = Math.max(0, viewPort.cameraX / tSize);
		int maxX = Math.min(GameState.getLevel().map.length - 1, (viewPort.cameraX + viewPort.width) / tSize) + 1;
		int minY = Math.max(0, viewPort.cameraY / tSize - 1);
		int maxY = Math.min(GameState.getLevel().map[0].length - 1, (viewPort.cameraY + viewPort.height) / tSize);
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				TextureRegion textureRegion = GameState.getLevel().map[x][y].animation.getKeyFrame(GameState.time(), true);
				batch.draw(textureRegion, (x * tSize - viewPort.cameraX), (y * tSize - viewPort.cameraY), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
			}
		}
	}

	public void toggleScene() {
		renderScene = !renderScene;
	}

	public void toggleLighting() {
		renderLighting = !renderLighting;
	}

	public void toggleHealthbars() {
		renderHealthbars = !renderHealthbars;
	}

	public void toggleBoundingBox() {
		renderBoundingBoxes = !renderBoundingBoxes;
	}

	public void toggleNoise() {
		renderNoise = !renderNoise;
	}

	public int getRenderCalls() {
		return renderCalls;
	}

	public float getFrameTime() {
		return frameTime;
	}

	@Override
	public void dispose() {
		batch.dispose();
		lightingBuffer.dispose();
		viewportBuffer.dispose();
		noiseBuffer.dispose();
		font.dispose();
	}

}
