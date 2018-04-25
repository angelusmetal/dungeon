package com.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.dungeon.engine.entity.Character;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.random.Rand;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

import java.util.Comparator;
import java.util.function.Predicate;

public class ViewPortRenderer {

	private final GameState state;
	private final ViewPort viewPort;

	private SpriteBatch batch;

	private ViewPortBuffer lightingBuffer;
	private ViewPortBuffer viewportBuffer;
	private BlendFunctionContext lightingContext;

	// Developer tools
	private boolean renderScene = true;
	private boolean renderLighting = true;
	private boolean renderHealthbars = true;

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

	public ViewPortRenderer(GameState state, ViewPort viewPort) {
		this.state = state;
		this.viewPort = viewPort;
		this.lightingBuffer = new ViewPortBuffer(viewPort);
		this.viewportBuffer = new ViewPortBuffer(viewPort);
		this.lightingContext = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		entityInCamera = (e) ->
			e.getPos().x < viewPort.cameraX + viewPort.cameraWidth &&
			e.getPos().x + e.getFrame(state.getStateTime()).getRegionWidth() > viewPort.cameraX &&
			e.getPos().y < viewPort.cameraY + viewPort.cameraHeight &&
			e.getPos().y + e.getFrame(state.getStateTime()).getRegionHeight() > viewPort.cameraY;
		lightInCamera = (e) ->
			e.getLight() != null &&
			e.getPos().x - e.getLight().diameter < viewPort.cameraX + viewPort.cameraWidth &&
			e.getPos().x + e.getLight().diameter > viewPort.cameraX &&
			e.getPos().y - e.getLight().diameter < viewPort.cameraY + viewPort.cameraHeight &&
			e.getPos().y + e.getLight().diameter > viewPort.cameraY;
	}

	public void initialize() {
		batch = new SpriteBatch();

		resetBuffers();

		randomizeBaseLight();
	}

	private void resetBuffers() {
		lightingBuffer.reset();
		viewportBuffer.reset();
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
				state.getEntities().stream().filter(e -> e instanceof Character).map(e -> (Character)e).sorted(comp).forEach(e -> e.drawHealthbar(state, batch, viewPort));
			});
		}

		batch.begin();
		viewportBuffer.drawScaled(batch);
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

	public void dispose() {
		batch.dispose();
		lightingBuffer.dispose();
	}

}
