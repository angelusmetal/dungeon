package com.dungeon.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;

import java.util.Comparator;

public class OverlayTextFragment implements RenderFragment {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer labelBuffer;
	private final Comparator<? super OverlayText> comp = (e1, e2) ->
			e1.getOrigin().y > e2.getOrigin().y ? -1 :
			e1.getOrigin().y < e2.getOrigin().y ? 1 :
			e1.getOrigin().x < e2.getOrigin().x ? -1 :
			e1.getOrigin().x > e2.getOrigin().x ? 1 : 0;
	private boolean enabled = true;

	public OverlayTextFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.labelBuffer = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		labelBuffer.reset();
	}

	@Override
	public void render() {
		if (enabled) {
			Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
			// TODO only draw what's on the viewport
			// Iterate texts in render order and draw them
			GameState.getOverlayTexts().stream()/*.filter(viewPort::isInViewPort)*/.sorted(comp).forEach(text -> {
				// Each text is rendered first in a separate buffer
				labelBuffer.render((batch) -> {
					Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					text.draw(batch, viewPort);
				});
				// And the buffer is then blended into the main scene
				viewportBuffer.render((batch) -> {
					Gdx.gl.glBlendFunc( GL20.GL_ONE,  GL20.GL_ONE_MINUS_SRC_ALPHA);
					batch.setColor(text.getColor());
					labelBuffer.draw(batch);
					batch.setColor(Color.WHITE);
				});
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {
		labelBuffer.dispose();
	}

}
