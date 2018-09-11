package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.resource.Resources;

public class TitleStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final ViewPortBuffer textBuffer;
	private boolean enabled = true;
	private GlyphLayout title;
	private GlyphLayout subtitle;
	private BitmapFont titleFont;
	private BitmapFont subtitleFont;
	private Color color = Color.WHITE.cpy();
	private float expiration;

	public TitleStage(ViewPort viewPort, ViewPortBuffer viewportBuffer) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.textBuffer = new ViewPortBuffer(viewPort, Pixmap.Format.RGBA8888);
		textBuffer.reset();
		titleFont = Resources.fonts.get("chantelli-antiqua-32");
		subtitleFont = Resources.fonts.get(Resources.DEFAULT_FONT);
	}

	@Override
	public void render() {
		if (enabled) {
			if (expiration > Engine.time()) {
				textBuffer.render(batch -> {
					titleFont.draw(batch, title,  viewPort.cameraWidth / 2 - title.width / 2, viewPort.cameraHeight / 2 + title.height);
					subtitleFont.draw(batch, subtitle,  viewPort.cameraWidth / 2 - subtitle.width / 2, viewPort.cameraHeight / 2 - subtitle.height);
				});
				viewportBuffer.render(batch -> {
					color.a = Util.clamp((expiration - Engine.time()) / 2f);
					batch.setColor(color);
					textBuffer.draw(batch);
					batch.setColor(Color.WHITE);
				});
			}
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {

	}
	
	public void display(String title) {
		this.title = new GlyphLayout(titleFont, title);
		this.subtitle = null;
		this.expiration = Engine.time() + 4;
	}

	public void display(String title, String subtitle) {
		this.title = new GlyphLayout(titleFont, title);
		this.subtitle = new GlyphLayout(subtitleFont, subtitle);
		this.expiration = Engine.time() + 4;
	}

}
