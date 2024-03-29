package com.dungeon.game.render.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.viewport.ViewPort;

import java.util.Comparator;

public class OverlayTextStage implements Renderer {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer labelBuffer;
	private final Comparator<? super OverlayText> comp = (e1, e2) ->
			e1.getOrigin().y > e2.getOrigin().y ? -1 :
			e1.getOrigin().y < e2.getOrigin().y ? 1 :
			e1.getOrigin().x < e2.getOrigin().x ? -1 :
			e1.getOrigin().x > e2.getOrigin().x ? 1 : 0;
	private ShaderProgram shaderOutline;

	public OverlayTextStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.labelBuffer = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		labelBuffer.reset();
		loadShader();
	}

	private void loadShader() {
		shaderOutline = Resources.shaders.get("df_vertex.glsl|outline_border_fragment.glsl");
	}

	@Override
	public void render() {
		Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
		// TODO only draw what's on the viewport
		// Iterate texts in render order and draw them
		labelBuffer.projectToViewPort();
		Engine.overlayTexts.stream().filter(t -> t.isInViewport(viewPort)).sorted(comp).forEach(text -> {
			// Each text is rendered first in a separate buffer
			labelBuffer.render(batch -> {
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				text.draw(batch);
			});
			viewportBuffer.projectToZero();
			viewportBuffer.render(batch -> {
				Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
				if (text.hasOutline()) {
					// If outline is enabled, draw the text first using the outline shader
					batch.end();
					shaderOutline.bind();
					shaderOutline.setUniformf("u_color", new Color(0f, 0f, 0f, text.getColor().a));
					shaderOutline.setUniformf("u_viewportInverse", new Vector2(1f / viewPort.width, 1f / viewPort.height));
					batch.setShader(shaderOutline);
					batch.begin();
					labelBuffer.draw(batch);
					batch.end();
					batch.setShader(null);
					batch.begin();
				}
				// And the buffer is then blended into the main scene
				batch.setColor(text.getColor());
				labelBuffer.draw(batch);
				batch.setColor(Color.WHITE);
			});
		});
	}

	@Override
	public void dispose() {
		labelBuffer.dispose();
	}

}
