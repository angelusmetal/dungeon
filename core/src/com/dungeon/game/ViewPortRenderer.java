package com.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.random.Rand;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.NoiseBuffer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

import java.util.Comparator;
import java.util.function.Predicate;

public class ViewPortRenderer implements Disposable {

	private final GameState state;
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

	private final Vector3 baseLight = new Vector3(0.3f, 0.2f, 0.1f);
	private final TextureRegion fill;

	private static final ColorContext BOUNDING_BOXES = new ColorContext(new Color(1f, 0.2f, 0.2f, 0.3f));

	public ViewPortRenderer(GameState state, ViewPort viewPort) {
		this.state = state;
		this.viewPort = viewPort;
		this.lightingBuffer = new ViewPortBuffer(viewPort);
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.noiseBuffer = new NoiseBuffer(state.getConfiguration());
		this.lightingContext = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.noiseContext = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		entityInCamera = (e) ->
			e.getPos().x - e.getDrawOffset().x < viewPort.cameraX + viewPort.cameraWidth &&
			e.getPos().x + e.getDrawOffset().x + e.getFrame(state.getStateTime()).getRegionWidth() > viewPort.cameraX &&
			e.getPos().y - e.getDrawOffset().y + e.getZPos() < viewPort.cameraY + viewPort.cameraHeight &&
			e.getPos().y + e.getDrawOffset().y + e.getZPos() + e.getFrame(state.getStateTime()).getRegionHeight() > viewPort.cameraY;
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
		float newX = Rand.nextFloat(1);
		float newY = Rand.nextFloat(1);
		float newZ = Rand.nextFloat(1);

		float attenuation = (newX + newY + newZ) / 0.6f;
		baseLight.set(newX / attenuation, newY / attenuation, newZ / attenuation);
	}

	public void render () {
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
				state.getEntities().stream().filter(entityInCamera).sorted(comp).forEach(e -> e.draw(state, batch, viewPort));
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
				lightingContext.run(batch, lightingBuffer::draw);
			});
		}

		// Render UI elements
		if (renderHealthbars) {
			viewportBuffer.render((batch) -> {
				state.getEntities().stream().filter(e -> e instanceof Character).filter(entityInCamera).map(e -> (Character)e).sorted(comp).forEach(e -> e.drawHealthbar(state, batch, viewPort));
			});
		}

		if (renderBoundingBoxes) {
			viewportBuffer.render((batch) -> {
				BOUNDING_BOXES.set(batch);
				state.getEntities().stream().filter(entityInCamera).forEach(e -> viewPort.draw(
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
		batch.end();

		batch.begin();
		font.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 10, viewPort.height - 10);
		batch.end();
	}

	private void renderLight() {
		lightingBuffer.render((batch) -> {
			// remember SpriteBatch's current functions
			int srcFunc = batch.getBlendSrcFunc();
			int dstFunc = batch.getBlendDstFunc();
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);

			Gdx.gl.glClearColor(baseLight.x, baseLight.y, baseLight.z, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			state.getEntities().stream().filter(lightInCamera).sorted(comp).forEach(e -> e.drawLight(state, batch, viewPort));

			// Restore blend function
			batch.setBlendFunction(srcFunc, dstFunc);
		});
	}

	private void drawMap(SpriteBatch batch) {
		// Only render the visible portion of the map
		int tWidth = state.getLevelTileset().tile_size;
		int tHeight = state.getLevelTileset().tile_size;
		int minX = Math.max(0, viewPort.cameraX / tWidth);
		int maxX = Math.min(state.getLevel().map.length - 1, (viewPort.cameraX + viewPort.width) / tWidth) + 1;
		int minY = Math.max(0, viewPort.cameraY / tHeight - 1);
		int maxY = Math.min(state.getLevel().map[0].length - 1, (viewPort.cameraY + viewPort.height) / tHeight);
		for (int x = minX; x < maxX; x++) {
			for (int y = maxY; y > minY; y--) {
				TextureRegion textureRegion = state.getLevel().map[x][y].animation.getKeyFrame(state.getStateTime(), true);
				batch.draw(textureRegion, (x * tWidth - viewPort.cameraX), (y * tHeight - viewPort.cameraY), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
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

	@Override
	public void dispose() {
		batch.dispose();
		lightingBuffer.dispose();
		viewportBuffer.dispose();
		noiseBuffer.dispose();
		font.dispose();
	}

}
