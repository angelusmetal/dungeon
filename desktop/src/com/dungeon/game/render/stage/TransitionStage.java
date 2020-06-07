package com.dungeon.game.render.stage;

import static com.dungeon.engine.util.Util.smoothstep;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

public class TransitionStage implements Renderer {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final TextureRegion fill;
	private final ShaderProgram shader;
	private float duration;
	private float start;
	private boolean open;
	private Runnable endAction;

	public TransitionStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		fill = new TextureRegion(Resources.textures.get("fill.png"));
		shader = Resources.shaders.get("df_vertex.glsl|transition/circle.glsl");
	}

	@Override
	public void render() {
		viewportBuffer.projectToZero();
		viewportBuffer.render(batch -> {
			batch.end();
			shader.begin();
			shader.setUniformf("u_bufferSize", new Vector2(viewPort.cameraWidth, viewPort.cameraHeight));
			float phase = smoothstep(start, start + duration, Engine.time());//(float) Math.sin(Engine.time());
			if (!open) {
				phase = 1f - phase;
			}
			shader.setUniformf("u_phase", phase);
			shader.end();
			batch.setShader(shader);
			batch.begin();
			batch.setColor(Color.BLACK);
			batch.draw(fill, 0, 0, viewPort.cameraWidth, viewPort.cameraHeight);
			batch.setColor(Color.WHITE);
			batch.setShader(null);
		});
		if (start + duration <= Engine.time() && endAction != null) {
			Game.scheduleUpdate(endAction);
			endAction = null;
		}
	}

	@Override
	public void dispose() {}

	public void open(float duration, Runnable endAction) {
		this.duration = duration;
		this.start = Engine.time();
		this.open = true;
		this.endAction = endAction;
	}

	public void close(float duration, Runnable endAction) {
		this.duration = duration;
		this.start = Engine.time();
		this.open = false;
		this.endAction = endAction;
	}
}
