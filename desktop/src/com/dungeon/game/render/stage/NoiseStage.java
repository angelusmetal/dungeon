package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.GL20;
import com.dungeon.engine.render.BlendFunctionContext;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.render.NoiseBuffer;

public class NoiseStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final NoiseBuffer noiseBuffer;
	private final BlendFunctionContext noiseContext;
	private boolean enabled = false;

	public NoiseStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.noiseBuffer = new NoiseBuffer(Game.getConfiguration());
		this.noiseContext = new BlendFunctionContext(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		noiseBuffer.renderNoise();
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render(batch -> {
				noiseContext.run(batch, () -> noiseBuffer.draw(batch, viewPort.cameraWidth, viewPort.cameraHeight));
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {
		noiseBuffer.dispose();
	}

}
