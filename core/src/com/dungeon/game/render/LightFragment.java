package com.dungeon.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;

import java.util.function.Predicate;

public class LightFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer lightBuffer;
	private final BlendFunctionContext combineLights;
	private final BlendFunctionContext blendLights;
	private final Predicate<? super Entity> lightInCamera;
	private final float gamma;
	private boolean enabled = true;

	public LightFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.lightBuffer = new ViewPortBuffer(viewPort);
		this.lightBuffer.reset();
		this.combineLights = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE);
		this.blendLights = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.gamma = GameState.getConfiguration().getDouble("viewport.gamma", 1.0d).floatValue();
		this.lightInCamera = (e) ->
				e.getLight() != null &&
				e.getPos().x - e.getLight().diameter < viewPort.cameraX + viewPort.cameraWidth &&
				e.getPos().x + e.getLight().diameter > viewPort.cameraX &&
				e.getPos().y - e.getLight().diameter < viewPort.cameraY + viewPort.cameraHeight &&
				e.getPos().y + e.getLight().diameter > viewPort.cameraY;
	}

	@Override
	public void render() {
		if (enabled) {
			// Render light in a separate frame buffer
			lightBuffer.render((batch) -> combineLights.run(batch, () -> {
				Gdx.gl.glClearColor(gamma, gamma, gamma, 1f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				GameState.getEntities().stream().filter(lightInCamera).forEach(e -> e.drawLight(batch, viewPort));
			}));
			// Draw lighting on top of scene
			viewportBuffer.render((batch) -> {
				batch.setColor(GameState.getBaseLight());
				blendLights.run(batch, () -> lightBuffer.draw(batch));
				batch.setColor(Color.WHITE);
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {
		lightBuffer.dispose();
	}

}
