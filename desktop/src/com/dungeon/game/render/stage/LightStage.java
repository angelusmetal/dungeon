package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

import java.util.function.Predicate;

public class LightStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer lightBuffer;
	private final BlendFunctionContext combineLights;
	private final BlendFunctionContext blendLights;
	private final float gamma;
	private boolean enabled = true;

	public LightStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.lightBuffer = new ViewPortBuffer(viewPort);
		this.lightBuffer.reset();
		this.lightBuffer.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.combineLights = new BlendFunctionContext(GL20.GL_ONE, GL20.GL_ONE);
		this.blendLights = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		this.gamma = Game.getConfiguration().getDouble("viewport.gamma", 1.0d).floatValue();
	}

	@Override
	public void render() {
		if (enabled) {
			// Render light in a separate frame buffer
			lightBuffer.render(batch -> combineLights.run(batch, () -> {
//				Gdx.gl.glClearColor(gamma, gamma, gamma, 1f);
				Gdx.gl.glClearColor(Engine.getBaseLight().r, Engine.getBaseLight().g, Engine.getBaseLight().b, 1f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				Engine.entities.inViewPort(viewPort, 100f).filter(viewPort::lightIsInViewPort).forEach(e -> e.drawLight(batch, viewPort));
			}));
			// Draw lighting on top of scene
			viewportBuffer.render(batch -> {
//				batch.setColor(GameState.getBaseLight());
				blendLights.run(batch, () -> lightBuffer.draw(batch));
//				batch.setColor(Color.WHITE);
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
